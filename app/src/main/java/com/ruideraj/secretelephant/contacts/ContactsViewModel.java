package com.ruideraj.secretelephant.contacts;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.ruideraj.secretelephant.AppLog;
import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.SingleLiveEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class ContactsViewModel extends ViewModel {

    public final MediatorLiveData<List<Contact>> phones = new MediatorLiveData<>();
    public final MediatorLiveData<List<Contact>> emails = new MediatorLiveData<>();

    public final List<Contact> selectedContacts = new ArrayList<>();
    public final Set<String> selectedData = new HashSet<>();

    public final MutableLiveData<String> emailAccount = new MutableLiveData<>();

    public final MediatorLiveData<Integer> showSelection = new MediatorLiveData<>();
    public final MutableLiveData<Integer> showProgress = new MutableLiveData<>();
    public final MutableLiveData<Boolean> showContinue = new MutableLiveData<>();

    public final SingleLiveEvent<Void> requestPermission = new SingleLiveEvent<>();
    public final SingleLiveEvent<Void> selectAccount = new SingleLiveEvent<>();
    public final SingleLiveEvent<Boolean> updatedContact = new SingleLiveEvent<>();
    public final SingleLiveEvent<Integer> toast = new SingleLiveEvent<>();
    public final SingleLiveEvent<Void> finish = new SingleLiveEvent<>();

    private ContactsRepository mContactsRepository;
    private AccountManager mAccountManager;

    private String searchText = null;
    private int contactsPermission = PackageManager.PERMISSION_DENIED;

    @Inject
    public ContactsViewModel(ContactsRepository contactsRepository, AccountManager accountManager) {
        mContactsRepository = contactsRepository;
        mAccountManager = accountManager;
        phones.addSource(mContactsRepository.getPhonesData(), phones::setValue);
        emails.addSource(mContactsRepository.getEmailsData(), emails::setValue);
        showSelection.addSource(phones, phoneList -> setListVisibility());
        showSelection.addSource(emails, emailList -> setListVisibility());

        showContinue.setValue(false);
        showSelection.setValue(View.GONE);
        showProgress.setValue(View.VISIBLE);
        emailAccount.setValue(null);
    }

    public void start(int permission) {
        contactsPermission = permission;
        setEmailAccount(mAccountManager.getAccount());

        if(contactsPermission == PackageManager.PERMISSION_GRANTED) {
            if(phones.getValue() == null && emails.getValue() == null) {
                AppLog.d("ContactsViewModel", "Start - Load Contacts");
                loadContacts();
            }
        }
        else {
            requestPermission.call();
        }
    }

    public void onRequestPermissionsResult(@NonNull int[] grantResults) {
        int result;
        if(grantResults.length == 0) {
            result = PackageManager.PERMISSION_DENIED;
        }
        else {
            result = grantResults[0];
        }
        contactsPermission = result;

        if(contactsPermission == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        }
        else {
            toast.setValue(R.string.contact_permission_contacts_denied);
            finish.call();
        }
    }

    public void loadContacts() {
        mContactsRepository.loadContacts();
    }

    private void setListVisibility() {
        if(phones.getValue() != null || emails.getValue() != null) {
            showSelection.setValue(View.VISIBLE);
            showProgress.setValue(View.GONE);
        }
        else {
            showSelection.setValue(View.GONE);
            showProgress.setValue(View.VISIBLE);
        }
    }

    public void signInResult(Task<GoogleSignInAccount> task) {
        try {
            setEmailAccount(task.getResult(ApiException.class));
        }
        catch (ApiException e) {
            switch(e.getStatusCode()) {
                case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                case CommonStatusCodes.NETWORK_ERROR:
                    toast.setValue(R.string.contact_google_sign_in_error);
                    break;
            }
        }
    }

    public void setEmailAccount(GoogleSignInAccount account) {
        if(account != null) {
            emailAccount.setValue(account.getEmail());
        }
        else {
            emailAccount.setValue(null);
        }
    }

    public void signInError(int errorCode) {

    }

    public Intent getSignInIntent() {
        return mAccountManager.getSignInIntent();
    }

    public void search(String text) {
        if(searchText != null && !searchText.equals(text) ||
                searchText == null && !TextUtils.isEmpty(text)) {
            if(!TextUtils.isEmpty(text)) text = text.toLowerCase();
            searchText = text;
            mContactsRepository.filter(text);
        }
    }

    public String getSearchText() {
        return searchText;
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
            boolean added;

            String data = contact.getData();
            if(selectedData.contains(data)) {
                selectedData.remove(data);

                for(Contact c : selectedContacts) {
                    if(c.getData().equals(data)) {
                        selectedContacts.remove(c);
                        break;
                    }
                }

                added = false;
            }
            else {
                selectedData.add(data);
                selectedContacts.add(contact);

                added = true;
            }

            if(selectedContacts.size() == 1) {
                showContinue.setValue(true);
            }
            else if(selectedContacts.size() == 0) {
                showContinue.setValue(false);
            }

            updatedContact.setValue(added);

            if(!TextUtils.isEmpty(searchText)) {
                search(null);
            }
        }
    }

    public void onSelectAccount() {
        selectAccount.call();
    }
}
