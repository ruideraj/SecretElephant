package com.ruideraj.secretelephant;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ruideraj.secretelephant.contacts.ContactsRepository;
import com.ruideraj.secretelephant.contacts.ContactsViewModel;
import com.ruideraj.secretelephant.injection.AppComponent;
import com.ruideraj.secretelephant.injection.ContactsComponent;
import com.ruideraj.secretelephant.injection.ContextModule;
import com.ruideraj.secretelephant.injection.MatchComponent;
import com.ruideraj.secretelephant.injection.SendComponent;
import com.ruideraj.secretelephant.main.MainViewModel;
import com.ruideraj.secretelephant.match.MatchViewModel;
import com.ruideraj.secretelephant.send.SendRepository;
import com.ruideraj.secretelephant.send.SendViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private Context mContext;

    public ViewModelFactory(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        AppComponent appComponent = ((SeApplication) mContext.getApplicationContext()).getAppComponent();

        if(modelClass.isAssignableFrom(MainViewModel.class)) {
            //noinspection unchecked
            return (T) appComponent.getMainViewModel();
        }
        else if(modelClass.isAssignableFrom(ContactsViewModel.class)) {
            ContactsComponent contactsComponent = appComponent.getContactsComponent();
            ContactsRepository contactsRepository = contactsComponent.contactsRepository();
            AccountManager accountManager = contactsComponent.accountManager();

            //noinspection unchecked
            return (T) new ContactsViewModel(contactsRepository, accountManager);
        }
        else if(modelClass.isAssignableFrom(MatchViewModel.class)) {
            MatchComponent matchComponent = appComponent.getMatchComponent();
            Runner runner = matchComponent.runner();

            //noinspection unchecked
            return (T) new MatchViewModel(runner);
        }
        else if (modelClass.isAssignableFrom(SendViewModel.class)) {
            SendComponent sendComponent = appComponent.getSendComponent(new ContextModule(mContext));
            SendRepository sendRepository = sendComponent.sendRepository();
            AccountManager accountManager = sendComponent.accountManager();

            //noinspection unchecked
            return (T) new SendViewModel(sendRepository, accountManager);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
