package com.ruideraj.secretelephant

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ruideraj.secretelephant.match.MatchActivity
import javax.inject.Inject

interface PermissionManager {

    fun checkPermission(permission: String): Boolean
    fun requestPermissions(permissions: Array<String>, requestCode: Int)

}

class PermissionManagerImpl @Inject constructor(private val activity: Activity) : PermissionManager {

    override fun checkPermission(permission: String) = ContextCompat
            .checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

    override fun requestPermissions(permissions: Array<String>, requestCode: Int)
            = ActivityCompat.requestPermissions(activity, permissions, requestCode)
}