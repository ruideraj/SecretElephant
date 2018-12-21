package com.ruideraj.secretelephant.injection;

import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.main.MainViewModel;

import dagger.Subcomponent;

@Subcomponent
public interface MainComponent {

    void inject(MainViewModel viewModel);
    AccountManager accountManager();

}
