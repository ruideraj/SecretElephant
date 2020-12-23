package com.ruideraj.secretelephant.contacts

import javax.inject.Inject
import javax.inject.Singleton

interface ContactsRepository {
    suspend fun loadContacts(): ContactsResult
}

@Singleton
class ContactsRepositoryImpl @Inject constructor(private val contactsProvider: ContactsProvider)
    : ContactsRepository {

    private var cachedResult: ContactsResult? = null

    override suspend fun loadContacts() = cachedResult ?: contactsProvider.loadContacts().also {
        cachedResult = it
    }

}