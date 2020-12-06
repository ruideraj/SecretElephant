package com.ruideraj.secretelephant.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.SingleLiveEvent;

import javax.inject.Inject;

public class MainViewModel extends ViewModel implements AccountManager.AccountListener {

    public final MutableLiveData<Boolean> signedIn = new MutableLiveData<>();
    public final SingleLiveEvent<Integer> signOutMessage = new SingleLiveEvent<>();

    private AccountManager mAccountManager;

    @Inject
    public MainViewModel(AccountManager accountManager) {
        mAccountManager = accountManager;
        signedIn.setValue(false);
    }

    public void start() {
        boolean accountPresent = mAccountManager.getAccount() != null;
        if(signedIn.getValue() != accountPresent) signedIn.setValue(accountPresent);
    }

    @Override
    public void onSignOutSuccess() {
        signedIn.setValue(false);
        signOutMessage.setValue(R.string.main_menu_signed_out);
    }

    @Override
    public void onSignOutFailure() {
        signOutMessage.setValue(R.string.main_menu_sign_out_failed);
    }

    public void signOut() {
        mAccountManager.signOut(this);
    }
}
