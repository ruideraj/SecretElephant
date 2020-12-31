package com.ruideraj.secretelephant

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ruideraj.secretelephant.contacts.ContactsViewModel
import com.ruideraj.secretelephant.injection.ActivityModule
import com.ruideraj.secretelephant.main.MainViewModel
import com.ruideraj.secretelephant.match.MatchViewModel
import com.ruideraj.secretelephant.send.SendViewModel

class ViewModelFactory constructor(private val activity: Activity): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appComponent = (activity.applicationContext as SeApplication).appComponent

        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return appComponent.mainViewModel() as T
            }
            modelClass.isAssignableFrom(ContactsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return appComponent.contactsViewModel() as T
            }
            modelClass.isAssignableFrom(MatchViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return appComponent.matchComponent(ActivityModule(activity)).matchViewModel() as T
            }
            modelClass.isAssignableFrom(SendViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return appComponent.sendComponent(ActivityModule(activity)).sendViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
        }
    }

}