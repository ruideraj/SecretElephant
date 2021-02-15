package com.ruideraj.secretelephant.match

import android.Manifest
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruideraj.secretelephant.*
import com.ruideraj.secretelephant.contacts.Contact
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MatchViewModel @Inject constructor(private val matchmaker: Matchmaker,
                                         private val permissionManager: PermissionManager,
                                         private val preferences: Preferences)
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

    val sendMessages: LiveData<MatchExchange>
        get() = sendMessagesData
    private val sendMessagesData = SingleLiveEvent<MatchExchange>()

    val showSmsPermissionRationale: SharedFlow<Unit>
        get() = showSmsPermissionRationaleFlow
    private val showSmsPermissionRationaleFlow = MutableSharedFlow<Unit>()

    val requestSmsPermission: SharedFlow<Unit>
        get() = requestSmsPermissionFlow
    private val requestSmsPermissionFlow = MutableSharedFlow<Unit>()

    val showAppSettingsDialog: SharedFlow<Unit>
        get() = showAppSettingsDialogFlow
    private val showAppSettingsDialogFlow = MutableSharedFlow<Unit>()

    val toast: LiveData<Int>
        get() = toastData
    private val toastData = SingleLiveEvent<Int>()

    fun setupExchange(intent: Intent) {
        val contacts = intent.getParcelableArrayListExtra<Contact>(KEY_SELECTED)
        if (contacts == null) {
            noContactsData.call()
            return
        }

        val mode = intent.getSerializableExtra(KEY_MODE) as Mode

        viewModelScope.launch {
            exchangeData.value = createExchange(contacts, mode)

            val text = if (mode == Mode.ELEPHANT) {
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
            if (containsPhones) {
                when {
                    permissionManager.checkPermission(Manifest.permission.SEND_SMS) -> sendMessagesData.value = exchange
                    preferences.matchShowPermissionDialog -> viewModelScope.launch {
                        showSmsPermissionRationaleFlow.emit(Unit)
                    }
                    else -> viewModelScope.launch { showAppSettingsDialogFlow.emit(Unit) }
                }
            } else {
                sendMessagesData.value = exchange
            }
        }
    }

    fun onPermissionRationaleResult(continued: Boolean) {
        preferences.matchShowPermissionDialog = false

        if (continued) {
            viewModelScope.launch { requestSmsPermissionFlow.emit(Unit) }
        } else {
            toastData.value = R.string.match_permission_denied_sms
        }
    }

    fun onSmsPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            sendMessagesData.value = exchangeData.value
        } else {
            toastData.value = R.string.match_permission_denied_sms
        }
    }

    private suspend fun createExchange(contacts: List<Contact>, mode: Mode): MatchExchange {
        val matches = matchmaker.match(contacts.size, mode == Mode.SANTA)
        return MatchExchange(contacts, matches, mode)
    }
}