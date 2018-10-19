package com.ruideraj.secretelephant.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.SingleLiveEvent;

public class MainViewModel extends ViewModel implements AccountManager.AccountListener {

    public final MutableLiveData<Boolean> signedIn = new MutableLiveData<>();
    public final SingleLiveEvent<Integer> signOutMessage = new SingleLiveEvent<>();

    private AccountManager mAccountManager;

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
        signedIn.postValue(false);
        signOutMessage.postValue(R.string.main_menu_signed_out);
    }

    @Override
    public void onSignOutFailure() {
        signOutMessage.postValue(R.string.main_menu_sign_out_failed);
    }

    public void signOut() {
        mAccountManager.signOut(this);
    }
}
