package com.ruideraj.secretelephant.contacts

import android.app.Application
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface ContactsProvider {
    suspend fun loadContacts(): ContactsResult
}

@Singleton
class ContactsDao @Inject constructor(private val application: Application): ContactsProvider {
    companion object {
        private val CONTACT_PROJECTION = arrayOf(ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.Contacts.HAS_PHONE_NUMBER)

        private val PHONES_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

        private val EMAILS_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS)
    }

    override suspend fun loadContacts() = withContext(Dispatchers.IO) {
        val phonesList = arrayListOf<Contact>()
        val emailsList = arrayListOf<Contact>()

        val where = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?"
        val selectionArgs = arrayOf("1")

        val cursor = application.contentResolver
                .query(ContactsContract.Contacts.CONTENT_URI, CONTACT_PROJECTION,
                        where, selectionArgs, ContactsContract.Contacts.DISPLAY_NAME)

        if (cursor != null) {
            val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val hasPhoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
            val photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)

            while (cursor.moveToNext()) {
                val contactId = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                val photoUri = cursor.getString(photoColumn)
                val hasPhone = cursor.getInt(hasPhoneColumn) > 0

                awaitAll(async {
                    if (hasPhone) {
                        phonesList.addAll(createPhoneContacts(contactId, name, photoUri))
                    }
                }, async {
                    emailsList.addAll(createEmailContacts(contactId, name, photoUri))
                })
            }

            cursor.close()
        }

        ContactsResult(phonesList, emailsList)
    }

    private fun createPhoneContacts(contactId: String, name: String, photoUri: String?): List<Contact> {
        val phones = mutableListOf<Contact>()

        val cursor = application.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(contactId), null)

        if (cursor != null) {
            val numberColumn = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(numberColumn)
                if (phoneNumber.isNotBlank()) {
                    phones.add(Contact(name, Contact.Type.PHONE, phoneNumber, photoUri))
                }
            }

            cursor.close()
        }

        return phones
    }

    private fun createEmailContacts(contactId: String, name: String, photoUri: String?): List<Contact> {
        val emails = mutableListOf<Contact>()

        val cursor = application.contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, EMAILS_PROJECTION,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(contactId), null)

        if (cursor != null) {
            val addressColumn = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Email.ADDRESS)

            while (cursor.moveToNext()) {
                val emailAddress = cursor.getString(addressColumn)
                if (emailAddress.isNotBlank()) {
                    emails.add(Contact(name, Contact.Type.EMAIL, emailAddress, photoUri))
                }
            }

            cursor.close()
        }

        return emails
    }

}