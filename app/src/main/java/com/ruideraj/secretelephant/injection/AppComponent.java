package com.ruideraj.secretelephant.injection;

import com.ruideraj.secretelephant.main.MainViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class, GoogleModule.class, RunnerModule.class})
@Singleton
public interface AppComponent {

    MainViewModel getMainViewModel();

    ContactsComponent getContactsComponent();
    MatchComponent getMatchComponent();
    SendComponent getSendComponent(ContextModule contextModule);

}
