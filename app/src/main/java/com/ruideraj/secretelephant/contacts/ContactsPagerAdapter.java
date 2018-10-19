package com.ruideraj.secretelephant.contacts;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ContactsPagerAdapter extends FragmentPagerAdapter {

    public ContactsPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case 0:
                fragment = ContactListFragment.newInstance(Contact.TYPE_PHONE);
                break;
            case 1:
                fragment = ContactListFragment.newInstance(Contact.TYPE_EMAIL);
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;  // Two tabs: Phones and Emails
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch(position) {
            case 0:
                title = "Phones";
                break;
            case 1:
                title = "Emails";
                break;
            default:
                title = "Contacts";
        }

        return title;
    }
}
