package com.ruideraj.secretelephant.injection

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.gmail.GmailScopes
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.AccountManagerImpl
import dagger.Module
import dagger.Provides

@Module
class AccountModule {

    @Provides
    fun accountManager(accountManagerImpl: AccountManagerImpl): AccountManager = accountManagerImpl

    @Provides
    fun googleSignInClient(application: Application): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder().requestEmail()
                .requestScopes(Scope(GmailScopes.GMAIL_SEND)).build()
        return GoogleSignIn.getClient(application, googleSignInOptions)
    }

}