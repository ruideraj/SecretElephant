package com.ruideraj.secretelephant.injection

import android.app.Activity
import android.content.Context
import com.ruideraj.secretelephant.PermissionManager
import com.ruideraj.secretelephant.PermissionManagerImpl
import dagger.Module
import dagger.Provides

@Module
class ActivityModule constructor (private val activity: Activity) {

    @Provides
    fun providesContext(): Context = activity

    @Provides
    fun providesActivity() = activity

    @Provides
    fun providesPermissionManager(permissionManagerImpl: PermissionManagerImpl): PermissionManager
            = permissionManagerImpl

}