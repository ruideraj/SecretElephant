package com.ruideraj.secretelephant.injection;

import com.ruideraj.secretelephant.AccountManager;

import dagger.Subcomponent;

@Subcomponent
public interface MainComponent {

    AccountManager accountManager();

}
