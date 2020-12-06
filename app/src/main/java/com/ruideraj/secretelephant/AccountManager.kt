package com.ruideraj.secretelephant

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(private val application: Application,
                                         private val googleClient: GoogleSignInClient) {

    fun getAccount() = GoogleSignIn.getLastSignedInAccount(application)

    fun getSignInIntent() = googleClient.signInIntent

    fun signOut(listener: AccountListener) {
        val task = googleClient.revokeAccess()
        task.addOnSuccessListener { listener.onSignOutSuccess() }
        task.addOnFailureListener { listener.onSignOutFailure() }
    }

    interface AccountListener {
        fun onSignOutSuccess()
        fun onSignOutFailure()
    }
}