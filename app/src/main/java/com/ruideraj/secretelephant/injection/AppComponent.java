package com.ruideraj.secretelephant.injection;

import com.ruideraj.secretelephant.contacts.ContactsViewModel;
import com.ruideraj.secretelephant.main.MainViewModel;
import com.ruideraj.secretelephant.match.MatchViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class, GoogleModule.class, RunnerModule.class})
@Singleton
public interface AppComponent {

    MainViewModel getMainViewModel();
    ContactsViewModel getContactsViewModel();
    MatchViewModel getMatchViewModel();

    SendComponent getSendComponent(ContextModule contextModule);

}
