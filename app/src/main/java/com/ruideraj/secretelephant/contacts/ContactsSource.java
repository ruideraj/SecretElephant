package com.ruideraj.secretelephant.contacts;

public interface ContactsSource {

    interface ContactsCallback {
        void onContactsLoaded(ContactsResult contactsResult);
    }

    void loadContacts(ContactsCallback callback);

    void clear();
}
