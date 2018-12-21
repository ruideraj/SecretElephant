package com.ruideraj.secretelephant.injection;

import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.contacts.ContactsRepository;

import dagger.Subcomponent;

@Subcomponent(modules = {ContactsModule.class})
public interface ContactsComponent {

    ContactsRepository contactsRepository();
    AccountManager accountManager();

}
