package com.ruideraj.secretelephant.contacts;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContactsRepository implements ContactsDao.DaoCallback {

    private ContactsDao mContactsDao;
    private ContactsResult mCachedResult;
    private FilterTask mPhonesFilterTask;
    private FilterTask mEmailsFilterTask;

    private final MutableLiveData<List<Contact>> mPhones = new MutableLiveData<>();
    private final MutableLiveData<List<Contact>> mEmails = new MutableLiveData<>();

    @Inject
    public ContactsRepository(ContactsDao dao) {
        mContactsDao = dao;
    }

    public MutableLiveData<List<Contact>> getPhonesData() {
        return mPhones;
    }

    public MutableLiveData<List<Contact>> getEmailsData() {
        return mEmails;
    }

    public void loadContacts() {
        // Load the user's phone and email contacts.
        if(mCachedResult == null) {
            mContactsDao.loadContacts(this);
        }
        else {
            mPhones.setValue(mCachedResult.phones);
            mEmails.setValue(mCachedResult.emails);
        }
    }

    public void filter(String constraint) {
        if(mCachedResult == null) return;

        if(mPhonesFilterTask != null && !mPhonesFilterTask.isCancelled()) {
            mPhonesFilterTask.cancel(true);
        }
        if(mEmailsFilterTask != null && !mEmailsFilterTask.isCancelled()) {
            mEmailsFilterTask.cancel(true);
        }

        mPhonesFilterTask = new FilterTask(Contact.TYPE_PHONE, constraint);
        mPhonesFilterTask.execute();
        mEmailsFilterTask = new FilterTask(Contact.TYPE_EMAIL, constraint);
        mEmailsFilterTask.execute();
    }

    public void clear() {
        mCachedResult = null;
    }

    @Override
    public void onContactsLoaded(ContactsResult contactsResult) {
        mCachedResult = contactsResult;
        mPhones.setValue(contactsResult.phones);
        mEmails.setValue(contactsResult.emails);
    }

    private class FilterTask extends AsyncTask<Void, Void, List<Contact>> {
        private int mType;
        private String mConstraint;

        public FilterTask(int type, String constraint) {
            mType = type;
            mConstraint = constraint;
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            List<Contact> filteredContacts;
            List<Contact> contacts =
                    mType == Contact.TYPE_PHONE ? mCachedResult.phones : mCachedResult.emails;

            if(mConstraint == null || mConstraint.isEmpty()) {
                filteredContacts = contacts;
            }
            else {
                filteredContacts = new ArrayList<>();
                for(Contact contact : contacts) {
                    if(contact.getName().toLowerCase().contains(mConstraint)) {
                        filteredContacts.add(contact);
                    }
                }
            }

            return filteredContacts;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            if(mType == Contact.TYPE_PHONE) {
                mPhones.setValue(contacts);
            }
            else {
                mEmails.setValue(contacts);
            }
        }
    }
}
