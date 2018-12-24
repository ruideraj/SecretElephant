package com.ruideraj.secretelephant.injection;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class, AccountModule.class, RunnerModule.class})
@Singleton
public interface AppComponent {

    MainComponent getMainComponent();
    ContactsComponent getContactsComponent();
    MatchComponent getMatchComponent();
    SendComponent getSendComponent(ContextModule contextModule);

}
