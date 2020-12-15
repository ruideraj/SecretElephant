package com.ruideraj.secretelephant.injection

import com.ruideraj.secretelephant.contacts.ContactsViewModel
import com.ruideraj.secretelephant.main.MainViewModel
import com.ruideraj.secretelephant.match.MatchViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, AccountModule::class, RunnerModule::class])
@Singleton
interface AppComponent {

    fun mainViewModel(): MainViewModel
    fun contactsViewModel(): ContactsViewModel
    fun matchViewModel(): MatchViewModel

    fun sendComponent(contextModule: ContextModule): SendComponent
}