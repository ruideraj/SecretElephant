package com.ruideraj.secretelephant.injection

import android.app.Application
import android.content.Context
import com.ruideraj.secretelephant.PermissionManager
import com.ruideraj.secretelephant.PermissionManagerImpl
import com.ruideraj.secretelephant.Preferences
import com.ruideraj.secretelephant.PreferencesImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule constructor (private val application: Application) {

    @Provides
    fun providesApplication() = application

    @Provides
    fun providesContext(): Context = application

    @Provides
    fun providesPermissionManager(permissionManagerImpl: PermissionManagerImpl): PermissionManager
            = permissionManagerImpl

    @Provides
    fun providesPreferences(preferencesImpl: PreferencesImpl): Preferences = preferencesImpl
}