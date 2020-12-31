package com.ruideraj.secretelephant.match

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruideraj.secretelephant.*
import com.ruideraj.secretelephant.contacts.Contact
import kotlinx.coroutines.launch
import javax.inject.Inject

class MatchViewModel @Inject constructor(private val matchmaker: Matchmaker,
                                         private val permissionManager: PermissionManager)
    : ViewModel() {

    val textId: LiveData<Int>
        get() = textIdData
    private val textIdData = MutableLiveData<Int>()

    val exchange: LiveData<MatchExchange>
        get() = exchangeData
    private val exchangeData = MutableLiveData<MatchExchange>()

    val noContacts: LiveData<Void>
        get() = noContactsData
    private val noContactsData = SingleLiveEvent<Void>()

    val sendInvites: LiveData<MatchExchange>
        get() = sendInvitesData
    private val sendInvitesData = SingleLiveEvent<MatchExchange>()

    val toast: LiveData<Int>
        get() = toastData
    private val toastData = SingleLiveEvent<Int>()

    fun setupExchange(intent: Intent) {
        val contacts = intent.getParcelableArrayListExtra<Contact>(KEY_SELECTED)
        if (contacts == null) {
            noContactsData.call()
            return
        }

        val mode = intent.getIntExtra(KEY_MODE, MODE_ELEPHANT)

        viewModelScope.launch {
            exchangeData.value = createExchange(contacts, mode)

            val text = if (mode == MODE_ELEPHANT) {
                R.string.match_elephant
            } else {
                R.string.match_santa
            }
            textIdData.value = text
        }
    }

    fun reorder() {
        val exchange = exchange.value
        if (exchange != null) {
            viewModelScope.launch {
                exchangeData.value = createExchange(exchange.contacts, exchange.mode)
            }
        }
    }

    fun send() {
        exchangeData.value?.let { exchange ->
            val containsPhones = exchange.contacts.any { it.type == Contact.Type.PHONE }
            val needSmsPermission = !permissionManager.checkPermission(Manifest.permission.SEND_SMS)
            if (containsPhones && needSmsPermission) {
                permissionManager.requestPermissions(arrayOf(Manifest.permission.SEND_SMS),
                        REQUEST_SMS)
            } else {
                sendInvitesData.value = exchange
            }
        }
    }

    fun onPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        if (requestCode == REQUEST_SMS) {
            if (results.isEmpty() || results[0] != PackageManager.PERMISSION_GRANTED) {
                toastData.value = R.string.match_permission_denied_sms
            } else {
                sendInvitesData.value = exchangeData.value
            }
        }
    }

    private suspend fun createExchange(contacts: List<Contact>, mode: Int): MatchExchange {
        val matches = matchmaker.match(contacts.size, mode == MODE_SANTA)
        return MatchExchange(contacts, matches, mode)
    }
}