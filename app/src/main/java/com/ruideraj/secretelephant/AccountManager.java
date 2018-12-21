package com.ruideraj.secretelephant;

import android.app.Application;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.gmail.GmailScopes;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountManager {

    private Application mApplication;
    private GoogleSignInClient mGoogleClient;

    @Inject
    public AccountManager(Application application) {
        mApplication = application;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder().requestEmail()
                .requestScopes(new Scope(GmailScopes.GMAIL_SEND)).build();
        mGoogleClient = GoogleSignIn.getClient(mApplication, gso);
    }

    public GoogleSignInAccount getAccount() {
        return GoogleSignIn.getLastSignedInAccount(mApplication);
    }

    public Intent getSignInIntent() {
        return mGoogleClient.getSignInIntent();
    }

    public void signOut(final AccountListener listener) {
        Task<Void> task = mGoogleClient.revokeAccess();
        task.addOnSuccessListener(aVoid -> listener.onSignOutSuccess());
        task.addOnFailureListener(exception -> listener.onSignOutFailure());
    }

    public interface AccountListener {
        void onSignOutSuccess();
        void onSignOutFailure();
    }
}
