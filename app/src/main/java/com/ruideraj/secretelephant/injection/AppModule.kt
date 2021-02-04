package com.ruideraj.secretelephant.injection

import android.app.Application
import android.content.Context
import com.ruideraj.secretelephant.PermissionManager
import com.ruideraj.secretelephant.PermissionManagerImpl
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
}