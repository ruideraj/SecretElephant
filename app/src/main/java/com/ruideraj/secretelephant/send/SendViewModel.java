package com.ruideraj.secretelephant.send;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.SingleLiveEvent;
import com.ruideraj.secretelephant.match.MatchExchange;

import java.util.List;

public class SendViewModel extends ViewModel implements SendAdapter.SendClickListener {

    public final MutableLiveData<List<SendInvite>> invitesData;
    public final SingleLiveEvent<Integer> updatedPosition = new SingleLiveEvent<>();
    public final SingleLiveEvent<Void> queueFinished = new SingleLiveEvent<>();

    public final SingleLiveEvent<Integer> toast = new SingleLiveEvent<>();
    public final MutableLiveData<Integer> listVisibility = new MutableLiveData<>();
    public final MutableLiveData<Integer> progressVisibility = new MutableLiveData<>();

    private SendRepository mRepository;
    private AccountManager mAccountManager;

    private Observer<int[]> mUpdateObserver;

    private int mTotalInvites;
    private int mInvitesSent;

    public SendViewModel(SendRepository repository, AccountManager accountManager) {
        mRepository = repository;
        invitesData = mRepository.getInvites();

        mUpdateObserver = (update) -> onUpdatedPosition(update);
        repository.getLastUpdatedPosition().observeForever(mUpdateObserver);

        mAccountManager = accountManager;

        progressVisibility.setValue(View.VISIBLE);
        listVisibility.setValue(View.GONE);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.getLastUpdatedPosition().removeObserver(mUpdateObserver);
    }

    void sendInvites(MatchExchange exchange) {
        setEmailAccount(mAccountManager.getAccount());

        mTotalInvites = exchange.getContacts().size();
        mInvitesSent = 0;

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

    private void onUpdatedPosition(int[] update) {
        int position = update[0];
        int status = update[1];

        if(status == SendInvite.SENT) {
            mInvitesSent++;
        }

        if(mInvitesSent == mTotalInvites) {
            toast.setValue(R.string.send_successful);
            queueFinished.call();
        }

        updatedPosition.setValue(position);
    }

    @Override
    public void onRefreshClick(int position) {
        mRepository.resend(position);
    }
}
