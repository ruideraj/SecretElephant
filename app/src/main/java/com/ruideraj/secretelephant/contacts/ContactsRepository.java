package com.ruideraj.secretelephant.contacts;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.ruideraj.secretelephant.ADB;

import java.util.ArrayList;
import java.util.List;

public class ContactsRepository implements ContactsSource, ContactsDao.DaoCallback {

    private static ContactsRepository INSTANCE;
    private static final Object sLock = new Object();

    private ContactsDao mContactsDao;
    private ContactsResult mCachedResult;
    private FilterTask mPhonesFilterTask;
    private FilterTask mEmailsFilterTask;

    private MutableLiveData<List<Contact>> mPhones = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> mEmails = new MutableLiveData<>();

    public static ContactsRepository getInstance(ContactsDao dao) {
        if(INSTANCE == null) {
            synchronized(sLock) {
                if(INSTANCE == null) {
                    INSTANCE = new ContactsRepository(dao);
                }
            }
        }

        return INSTANCE;
    }

    public ContactsRepository(ContactsDao dao) {
        mContactsDao = dao;
    }

    @Override
    public MutableLiveData<List<Contact>> getPhonesData() {
        return mPhones;
    }

    @Override
    public MutableLiveData<List<Contact>> getEmailsData() {
        return mEmails;
    }

    @Override
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

    @Override
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

    @Override
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
