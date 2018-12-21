package com.ruideraj.secretelephant.injection;

import android.app.Application;

import com.ruideraj.secretelephant.contacts.ContactsDao;
import com.ruideraj.secretelephant.contacts.ContactsRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class ContactsModule {

    @Provides
    ContactsRepository providesContactsRepository(ContactsDao dao) {
        return new ContactsRepository(dao);
    }

    @Provides
    ContactsDao providesContactsDao(Application application) {
        return new ContactsDao(application);
    }
}
