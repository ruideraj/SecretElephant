package com.ruideraj.secretelephant.send;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.match.MatchExchange;

import java.util.List;

public class SendViewModel extends ViewModel implements SendAdapter.SendClickListener {

    public final MutableLiveData<List<SendInvite>> invitesData;
    public final MutableLiveData<Integer> updatedPosition;

    public final MutableLiveData<Integer> listVisibility = new MutableLiveData<>();
    public final MutableLiveData<Integer> progressVisibility = new MutableLiveData<>();

    private SendRepository mRepository;
    private AccountManager mAccountManager;

    public SendViewModel(SendRepository repository, AccountManager accountManager) {
        mRepository = repository;
        invitesData = mRepository.getInvites();
        updatedPosition = mRepository.getLastUpdatedPosition();
        mAccountManager = accountManager;

        progressVisibility.setValue(View.VISIBLE);
        listVisibility.setValue(View.GONE);
    }

    public void sendInvites(MatchExchange exchange) {
        setEmailAccount(mAccountManager.getAccount());
        mRepository.send(exchange);

        progressVisibility.setValue(View.GONE);
        listVisibility.setValue(View.VISIBLE);
    }

    private void setEmailAccount(GoogleSignInAccount account) {
        if(account != null) {
            mRepository.setEmailAccount(account.getEmail());
        }
        else {
            mRepository.setEmailAccount(null);
        }
    }

    @Override
    public void onRefreshClick(int position) {
        mRepository.resend(position);
    }
}
