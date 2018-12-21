package com.ruideraj.secretelephant.injection;

import android.app.Application;

import com.ruideraj.secretelephant.AccountManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AccountModule {

    @Provides
    @Singleton
    static AccountManager providesAccountManager(Application application) {
        return new AccountManager(application);
    }

}
