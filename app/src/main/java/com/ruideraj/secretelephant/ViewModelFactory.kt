package com.ruideraj.secretelephant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ruideraj.secretelephant.contacts.ContactsViewModel
import com.ruideraj.secretelephant.injection.ContextModule
import com.ruideraj.secretelephant.main.MainViewModel
import com.ruideraj.secretelephant.match.MatchViewModel
import com.ruideraj.secretelephant.send.SendViewModel

class ViewModelFactory constructor(private val context: Context): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appComponent = (context.applicationContext as SeApplication).appComponent

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return appComponent.mainViewModel() as T
        } else if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return appComponent.contactsViewModel() as T
        } else if (modelClass.isAssignableFrom(MatchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return appComponent.matchViewModel() as T
        } else if (modelClass.isAssignableFrom(SendViewModel::class.java)) {
            val sendComponent = appComponent.sendComponent(ContextModule(context))

            @Suppress("UNCHECKED_CAST")
            return sendComponent.sendViewModel() as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

}