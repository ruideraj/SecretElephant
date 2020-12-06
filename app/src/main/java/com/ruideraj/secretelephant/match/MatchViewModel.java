package com.ruideraj.secretelephant.match;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Intent;

import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.Runner;
import com.ruideraj.secretelephant.SingleLiveEvent;
import com.ruideraj.secretelephant.contacts.Contact;

import java.util.ArrayList;

import javax.inject.Inject;

public class MatchViewModel extends ViewModel {

    public MutableLiveData<Integer> textId = new MutableLiveData<>();
    public MutableLiveData<MatchExchange> exchange = new MutableLiveData<>();

    public SingleLiveEvent<Void> noContacts = new SingleLiveEvent<>();

    public Runner mRunner;
    public AccountManager mAccountManager;

    @Inject
    public MatchViewModel(Runner runner, AccountManager accountManager) {
        mRunner = runner;
        mAccountManager = accountManager;
    }

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

            OrderTask orderTask = new OrderTask(contacts, mode);
            mRunner.runBackground(orderTask);
        }
    }

    public void reorder() {
        MatchExchange ex = exchange.getValue();
        if(ex != null) {
            int[] order = getOrder(ex.getContacts().size(), ex.getMode());
            ex.setMatches(order);
            exchange.setValue(ex);
        }
    }

    private int[] getOrder(int size, int mode) {
        boolean checkOrder = mode == Constants.MODE_SANTA;
        return MatchMaker.match(size, checkOrder);
    }

    // Ordering done inside a background Runnable in case of large number
    // of selected contacts or if in the future we implement more sophisticated ordering logic
    // e.g. exclusion rules
    private class OrderTask implements Runnable {
        ArrayList<Contact> mContacts;
        int mMode;

        public OrderTask(ArrayList<Contact> contacts, int mode) {
            mContacts = contacts;
            mMode = mode;
        }

        @Override
        public void run() {
            int[] order = getOrder(mContacts.size(), mMode);
            OrderUpdateTask updateTask = new OrderUpdateTask(mContacts, order, mMode);
            mRunner.runUi(updateTask);
        }
    }

    private class OrderUpdateTask implements Runnable {
        MatchExchange ex;

        public OrderUpdateTask(ArrayList<Contact> contacts, int[] order, int mode) {
            ex = new MatchExchange(contacts, order, mode);
        }

        @Override
        public void run() {
            exchange.setValue(ex);
        }
    }
}
