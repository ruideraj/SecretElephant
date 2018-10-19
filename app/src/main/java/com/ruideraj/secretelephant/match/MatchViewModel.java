package com.ruideraj.secretelephant.match;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;

import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.SingleLiveEvent;
import com.ruideraj.secretelephant.contacts.Contact;

import java.util.ArrayList;

public class MatchViewModel extends ViewModel {

    public MutableLiveData<Integer> textId = new MutableLiveData<>();
    public MatchExchangeLiveData exchange = new MatchExchangeLiveData();

    public SingleLiveEvent<Void> noContacts = new SingleLiveEvent<>();

    public void processIntent(Intent intent) {
        ArrayList<Contact> contacts = intent.getParcelableArrayListExtra(Constants.KEY_SELECTED);
        if(contacts == null) {
            noContacts.call();
            return;
        }
        int mode = intent.getIntExtra(Constants.KEY_MODE, Constants.MODE_ELEPHANT);

        createMatches(contacts, mode);
    }

    public void createMatches(ArrayList<Contact> contacts, int mode) {
        if(contacts != null) {
            if(mode == Constants.MODE_SANTA) {
                textId.setValue(R.string.match_santa);
            }
            else if(mode == Constants.MODE_ELEPHANT) {
                textId.setValue(R.string.match_elephant);
            }

            exchange.createExchange(contacts, mode);
        }
    }

    public void reorder() {
        MatchExchange ex = exchange.getValue();
        if(ex != null) {
            createMatches(ex.getContacts(), ex.getMode());
        }
    }
}
