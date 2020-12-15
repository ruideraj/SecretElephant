package com.ruideraj.secretelephant.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.SingleLiveEvent
import javax.inject.Inject

class MainViewModel @Inject constructor (private val accountManager: AccountManager): ViewModel() {

    val signedIn: LiveData<Boolean>
        get() = signedInData
    private val signedInData = MutableLiveData(false)

    val signOutMessage: LiveData<Int>
        get() = signOutMessageData
    private val signOutMessageData = SingleLiveEvent<Int>()

    fun start() {
        val accountPresent = accountManager.getAccount() != null
        if (signedInData.value != accountPresent) signedInData.value = accountPresent
    }

    fun signOut() {
        accountManager.signOut(object : AccountManager.AccountListener {
            override fun onSignOutSuccess() {
                signedInData.value = false
                signOutMessageData.value = R.string.main_menu_signed_out
            }

            override fun onSignOutFailure() {
                signOutMessageData.value = R.string.main_menu_sign_out_failed
            }
        })
    }
}