package com.ruideraj.secretelephant

import android.app.Application
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import javax.inject.Inject
import javax.inject.Singleton

interface AccountManager {
    fun getAccount(): GoogleSignInAccount?
    fun getSignInIntent(): Intent
    fun signOut(listener: AccountListener)

    interface AccountListener {
        fun onSignOutSuccess()
        fun onSignOutFailure()
    }
}

@Singleton
class AccountManagerImpl @Inject constructor(private val application: Application,
                                         private val googleClient: GoogleSignInClient): AccountManager {

    override fun getAccount() = GoogleSignIn.getLastSignedInAccount(application)

    override fun getSignInIntent() = googleClient.signInIntent

    override fun signOut(listener: AccountManager.AccountListener) {
        // TODO Convert this sign-out process to use a suspend function/coroutines
        val task = googleClient.revokeAccess()
        task.addOnSuccessListener { listener.onSignOutSuccess() }
        task.addOnFailureListener { listener.onSignOutFailure() }
    }

}