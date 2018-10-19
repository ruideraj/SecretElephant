package com.ruideraj.secretelephant.match;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.contacts.Contact;

import java.util.ArrayList;

public class MatchExchangeLiveData extends MutableLiveData<MatchExchange> {

    public void createExchange(ArrayList<Contact> contacts, int mode) {
        CreateExchangeTask task = new CreateExchangeTask(contacts, mode);
        task.execute();
    }

    private class CreateExchangeTask extends AsyncTask<Void, Void, MatchExchange> {

        private ArrayList<Contact> mContacts;
        private int mMode;

        public CreateExchangeTask(ArrayList<Contact> contacts, int mode) {
            mContacts = contacts;
            mMode = mode;
        }

        @Override
        protected MatchExchange doInBackground(Void... voids) {
            int[] matches = MatchMaker.match(mContacts.size(),mMode == Constants.MODE_SANTA);
            return new MatchExchange(mContacts, matches, mMode);
        }

        @Override
        protected void onPostExecute(MatchExchange matchExchange) {
            setValue(matchExchange);
        }
    }
}
