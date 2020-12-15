package com.ruideraj.secretelephant.injection

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class AppModule constructor (private val application: Application) {

    @Provides
    fun providesApplication() = application

}