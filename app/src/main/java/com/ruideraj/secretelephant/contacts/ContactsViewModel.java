package com.ruideraj.secretelephant.contacts;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.SingleLiveEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsViewModel extends ViewModel {

    public MutableLiveData<List<Contact>> phones;
    public MutableLiveData<List<Contact>> emails;

    public final List<Contact> selectedContacts = new ArrayList<>();
    public final Set<String> selectedData = new HashSet<>();
    public String searchText = null;

    public final MutableLiveData<String> emailAccount = new MutableLiveData<>();
    //public final MutableLiveData<String> searchText = new MutableLiveData<>();
    public final MediatorLiveData<Boolean> showLists = new MediatorLiveData<>();
    public final MutableLiveData<Boolean> showContinue = new MutableLiveData<>();
    public final MutableLiveData<GoogleSignInAccount> googleAccount = new MutableLiveData<>();

    public final SingleLiveEvent<Void> selectAccount = new SingleLiveEvent<>();
    public final SingleLiveEvent<Contact> updatedContact = new SingleLiveEvent<>();

    private ContactsSource mContactsSource;
    private AccountManager mAccountManager;

    public ContactsViewModel(ContactsSource contactsSource, AccountManager accountManager) {
        mContactsSource = contactsSource;
        mAccountManager = accountManager;
        phones = mContactsSource.getPhonesData();
        emails = mContactsSource.getEmailsData();
        showLists.addSource(phones, phoneList -> setListVisibility());
        showLists.addSource(emails, emailList -> setListVisibility());

        showContinue.setValue(false);
        emailAccount.setValue("");
    }

    public void start() {
        GoogleSignInAccount account = mAccountManager.getAccount();

        // TODO Check if this null check is necessary, how is it updated if account is revoked?
        if(account != null) {
            googleAccount.postValue(account);
        }
    }

    public void loadContacts() {
        mContactsSource.loadContacts();
    }

    public void setListVisibility() {
        showLists.setValue(phones.getValue() != null || emails.getValue() != null);
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
        if(searchText != null && !searchText.equals(text) ||
                searchText == null && !TextUtils.isEmpty(text)) {
            if(!TextUtils.isEmpty(text)) text = text.toLowerCase();
            searchText = text;
            mContactsSource.filter(text);
        }
    }

    public void onContactClicked(int type, int position) {

        List<Contact> list = null;
        if(type == Contact.TYPE_PHONE) {
            list = phones.getValue();
        }
        else if(type == Contact.TYPE_EMAIL) {
            list = emails.getValue();
        }

        if(list != null) {
            Contact contact = list.get(position);

            String data = contact.getData();
            if(selectedData.contains(data)) {
                selectedData.remove(data);

                for(Contact c : selectedContacts) {
                    if(c.getData().equals(data)) {
                        selectedContacts.remove(c);
                        break;
                    }
                }
            }
            else {
                selectedData.add(data);
                selectedContacts.add(contact);
            }

            if(selectedContacts.size() == 1) {
                showContinue.postValue(true);
            }
            else if(selectedContacts.size() == 0) {
                showContinue.postValue(false);
            }

            updatedContact.postValue(contact);

            if(!TextUtils.isEmpty(searchText)) {
                search(null);
            }
        }
    }

    public void onSelectAccount() {
        selectAccount.call();
    }
}
