package com.ruideraj.secretelephant.contacts;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

public interface ContactsSource {

    MutableLiveData<List<Contact>> getPhonesData();

    MutableLiveData<List<Contact>> getEmailsData();

    void loadContacts();

    void filter(String constraint);

    void clear();
}
