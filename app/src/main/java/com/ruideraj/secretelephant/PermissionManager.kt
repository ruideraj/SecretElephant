package com.ruideraj.secretelephant

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject

interface PermissionManager {

    fun checkPermission(permission: String): Boolean

}

class PermissionManagerImpl @Inject constructor(private val context: Context) : PermissionManager {

    override fun checkPermission(permission: String) = ContextCompat
            .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

}