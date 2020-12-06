package com.ruideraj.secretelephant

import androidx.multidex.MultiDexApplication
import com.ruideraj.secretelephant.injection.AppComponent
import com.ruideraj.secretelephant.injection.AppModule
import com.ruideraj.secretelephant.injection.DaggerAppComponent

class SeApplication : MultiDexApplication() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

}