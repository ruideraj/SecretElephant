package com.ruideraj.secretelephant;

import androidx.multidex.MultiDexApplication;

import com.ruideraj.secretelephant.injection.AppComponent;
import com.ruideraj.secretelephant.injection.AppModule;
import com.ruideraj.secretelephant.injection.DaggerAppComponent;

public class SeApplication extends MultiDexApplication {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
