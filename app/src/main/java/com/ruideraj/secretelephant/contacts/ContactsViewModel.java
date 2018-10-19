package com.ruideraj.secretelephant.contacts;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class ContactsViewModel extends ViewModel implements ContactsSource.ContactsCallback {

    public final MutableLiveData<ContactsResult> contacts = new MutableLiveData<>();
    public final List<Contact> selectedContacts = new ArrayList<>();
    public final MutableLiveData<String> emailAccount = new MutableLiveData<>();
    public final MutableLiveData<String> searchText = new MutableLiveData<>();
    public final MutableLiveData<Boolean> showContinue = new MutableLiveData<>();
    public final MutableLiveData<GoogleSignInAccount> googleAccount = new MutableLiveData<>();

    public final SingleLiveEvent<Void> selectAccount = new SingleLiveEvent<>();
    public final SingleLiveEvent<Contact> updatedContact = new SingleLiveEvent<>();

    private ContactsSource mContactsSource;
    private AccountManager mAccountManager;

    public ContactsViewModel(ContactsSource contactsSource, AccountManager accountManager) {
        mContactsSource = contactsSource;
        mAccountManager = accountManager;
        showContinue.setValue(false);
        emailAccount.setValue("");
    }

    @Override
    public void onContactsLoaded(ContactsResult contactsResult) {
        contacts.postValue(contactsResult);
    }

    public void start() {
        GoogleSignInAccount account = mAccountManager.getAccount();
        if(account != null) {
            googleAccount.postValue(account);
        }
    }

    public void loadContacts() {
        mContactsSource.loadContacts(this);
    }

    public void setGoogleAccount(GoogleSignInAccount account) {
        googleAccount.postValue(account);
    }

    public void setEmailAccount(String email) {
        emailAccount.postValue(email);
    }

    public Intent getSignInIntent() {
        return mAccountManager.getSignInIntent();
    }

    public void search(String text) {
        if(searchText.getValue() != null && !searchText.getValue().equals(text) ||
                searchText.getValue() == null && !TextUtils.isEmpty(text)) {
            if(!TextUtils.isEmpty(text)) text = text.toLowerCase();
            searchText.postValue(text);
        }
    }

    public void onContactClicked(Contact contact) {
        ContactsResult contactsResult = contacts.getValue();
        if(contactsResult == null) return;

        List<Contact> list = null;
        if(contact.getType() == Contact.TYPE_PHONE) {
            list = contactsResult.phones;
        }
        else if(contact.getType() == Contact.TYPE_EMAIL) {
            list = contactsResult.emails;
        }

        if(list != null) {
            boolean selected = !contact.isSelected();
            contact.setSelected(selected);

            if(selected) {
                selectedContacts.add(contact);
            }
            else {
                for(Contact c : selectedContacts) {
                    if(c.getData().equals(contact.getData())) {
                        selectedContacts.remove(c);
                        break;
                    }
                }
            }

            if(selectedContacts.size() == 1) {
                showContinue.postValue(true);
            }
            else if(selectedContacts.size() == 0) {
                showContinue.postValue(false);
            }

            updatedContact.postValue(contact);

            if(!TextUtils.isEmpty(searchText.getValue())) {
                searchText.postValue(null);
            }
        }
    }

    public void onSelectAccount() {
        selectAccount.call();
    }
}
