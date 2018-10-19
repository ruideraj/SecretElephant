package com.ruideraj.secretelephant.send;

import android.app.Application;
import android.app.Service;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ruideraj.secretelephant.match.MatchExchange;

import java.util.List;

public class SendViewModel extends AndroidViewModel implements
        ServiceConnection, SendService.SendListener, SendAdapter.SendClickListener {

    public MutableLiveData<List<SendInvite>> invitesData = new MutableLiveData<>();
    public MutableLiveData<Integer> updatedPosition = new MutableLiveData<>();

    private SendService.SendBinder mBinder;
    private MatchExchange mExchange;

    public SendViewModel(Application application) {
        super(application);
    }

    public void sendInvites(MatchExchange exchange) {
        Intent serviceIntent = new Intent(getApplication(), SendService.class);
        mExchange = exchange;
        getApplication().bindService(serviceIntent, this, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBinder = (SendService.SendBinder) service;
        mBinder.registerListener(this);
        mBinder.sendInvites(mExchange);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBinder.unregisterListener(this);
        getApplication().unbindService(this);
        mBinder = null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mBinder.unregisterListener(this);
        getApplication().unbindService(this);
        mBinder = null;
    }

    @Override
    public void sendListCreated(List<SendInvite> invites) {
        invitesData.postValue(invites);
    }

    @Override
    public void sendItemUpdated(int position) {
        updatedPosition.postValue(position);
    }

    @Override
    public void sendCompleted() {
        // TODO
        // Could tell Activity to go back to main menu once all invites are sent.
    }

    @Override
    public void onRefreshClick(int position) {
        mBinder.resendInvite(position);
    }
}
