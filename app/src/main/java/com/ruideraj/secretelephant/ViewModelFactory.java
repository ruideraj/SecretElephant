package com.ruideraj.secretelephant;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ruideraj.secretelephant.contacts.ContactsRepository;
import com.ruideraj.secretelephant.contacts.ContactsSource;
import com.ruideraj.secretelephant.contacts.ContactsViewModel;
import com.ruideraj.secretelephant.main.MainViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory INSTANCE;
    private static final Object sLock = new Object();

    private Application mApplication;

    public static ViewModelFactory getInstance(Application application) {
        if(INSTANCE == null) {
            synchronized(sLock) {
                if(INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application);
                }
            }
        }

        return INSTANCE;
    }

    private ViewModelFactory(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(MainViewModel.class)) {
            AccountManager accountManager = AccountManager.getInstance(mApplication);

            //noinspection unchecked
            return (T) new MainViewModel(accountManager);
        }
        else if(modelClass.isAssignableFrom(ContactsViewModel.class)) {
            AccountManager accountManager = AccountManager.getInstance(mApplication);
            ContactsSource contactsSource = ContactsRepository.getInstance(mApplication);

            //noinspection unchecked
            return (T) new ContactsViewModel(contactsSource, accountManager);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
