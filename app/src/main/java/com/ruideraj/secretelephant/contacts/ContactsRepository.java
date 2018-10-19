package com.ruideraj.secretelephant.contacts;

import android.app.Application;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.ruideraj.secretelephant.ADB;

import java.util.ArrayList;

public class ContactsRepository implements ContactsSource {

    private static String[] CONTACT_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.Contacts.HAS_PHONE_NUMBER};

    private static String[] PHONES_PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.NUMBER };

    private static String[] EMAILS_PROJECTION = {
            ContactsContract.CommonDataKinds.Email.DATA };

    private static ContactsRepository INSTANCE;
    private static final Object sLock = new Object();

    private Application mApplication;
    private ContactsResult mCachedResult;

    public static ContactsRepository getInstance(Application application) {
        if(INSTANCE == null) {
            synchronized(sLock) {
                if(INSTANCE == null) {
                    INSTANCE = new ContactsRepository(application);
                }
            }
        }

        return INSTANCE;
    }

    public ContactsRepository(Application application) {
        mApplication = application;
    }

    @Override
    public void loadContacts(ContactsCallback callback) {
        // Load the user's phone and email contacts.
        if(mCachedResult == null) {
            LoadContactsTask task = new LoadContactsTask(callback);
            task.execute(mApplication);
        }
        else {
            callback.onContactsLoaded(mCachedResult);
        }
    }

    @Override
    public void clear() {
        mCachedResult = null;
    }

    private class LoadContactsTask extends AsyncTask<Application, Void, ContactsResult> {
        ContactsSource.ContactsCallback mCallback;

        public LoadContactsTask(ContactsSource.ContactsCallback callback) {
            mCallback = callback;
        }

        @Override
        protected ContactsResult doInBackground(Application... applications) {
            Application application = applications[0];
            ADB.d("ContactsActivity", "ContactsLiveData doInBackground");
            ArrayList<Contact> phonesList = new ArrayList<>();
            ArrayList<Contact> emailList = new ArrayList<>();

            // only want Contacts that are visible to the user, i.e. within the
            // Contacts app
            String where = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?";
            String[] selectionArgs = new String[] { "1" };

            // get Cursor object with device's contact data sorted by display name
            Cursor cursor = application.getContentResolver()
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            CONTACT_PROJECTION, where, selectionArgs,
                            ContactsContract.Contacts.DISPLAY_NAME);

            if(cursor != null) {
                Contact newContact;
                String contactId;
                String name;

                // loop each contact to get data
                while (cursor.moveToNext()) {
                    contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    name = cursor.getString((cursor.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME)));

                    // Get contact's phone numbers if available
                    if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor phoneCursor = application.getContentResolver()
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        PHONES_PROJECTION,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = ?", new String[] { contactId }, null);

                        // add all of contact's phone numbers to list
                        if(phoneCursor != null) {
                            int phoneDataColumn = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            String phoneNumber;
                            while (phoneCursor.moveToNext()) {
                                phoneNumber = phoneCursor
                                        .getString(phoneDataColumn);
                                if (phoneNumber != null && phoneNumber.trim().length() > 0) {
                                    newContact = new Contact(name, Contact.TYPE_PHONE, phoneNumber);
                                    phonesList.add(newContact);
                                }
                            }
                            phoneCursor.close();
                        }
                    }

                    // retrieve email addresses of contact
                    Cursor emailCursor = application.getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                    EMAILS_PROJECTION,
                                    ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                            + " = ?", new String[] { contactId }, null);

                    // add all of contacts email addresses to list
                    if(emailCursor != null) {
                        int emailDataColumn = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                        String emailAddress;
                        while (emailCursor.moveToNext()) {
                            emailAddress = emailCursor
                                    .getString(emailDataColumn);
                            if (emailAddress != null
                                    && emailAddress.trim().length() > 4) {
                                newContact = new Contact(name, Contact.TYPE_EMAIL, emailAddress);
                                emailList.add(newContact);
                            }
                        }
                        emailCursor.close();
                    }
                }

                cursor.close();
            }

            ContactsResult result = new ContactsResult();
            result.phones = phonesList;
            result.emails = emailList;

            return result;
        }

        @Override
        protected void onPostExecute(ContactsResult contactsResult) {
            mCachedResult = contactsResult;
            mCallback.onContactsLoaded(contactsResult);
        }
    }
}
