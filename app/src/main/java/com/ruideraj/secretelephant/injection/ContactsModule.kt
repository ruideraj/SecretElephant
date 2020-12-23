package com.ruideraj.secretelephant.injection

import com.ruideraj.secretelephant.contacts.ContactsDao
import com.ruideraj.secretelephant.contacts.ContactsProvider
import com.ruideraj.secretelephant.contacts.ContactsRepository
import com.ruideraj.secretelephant.contacts.ContactsRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class ContactsModule {

    @Provides
    fun providesContactsRepository(contactsRepoImpl: ContactsRepositoryImpl): ContactsRepository
            = contactsRepoImpl

    @Provides
    fun providesContactsProvider(contactsDao: ContactsDao): ContactsProvider = contactsDao

}