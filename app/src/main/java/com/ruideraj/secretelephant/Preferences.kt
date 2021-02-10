package com.ruideraj.secretelephant

import android.content.Context
import androidx.preference.PreferenceManager
import javax.inject.Inject

interface Preferences {
    var contactsShowPermissionDialog: Boolean
}

class PreferencesImpl @Inject constructor (context: Context) : Preferences {

    private companion object {
        private const val CONTACTS_SHOW_PERMISSION_DIALOG = "contactsPermissionShowDialog"
    }

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override var contactsShowPermissionDialog: Boolean
        get() = sharedPrefs.getBoolean(CONTACTS_SHOW_PERMISSION_DIALOG, true)
        set(value) = sharedPrefs.edit().putBoolean(CONTACTS_SHOW_PERMISSION_DIALOG, value).apply()

}