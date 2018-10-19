package com.ruideraj.secretelephant.match;

import android.os.Parcel;
import android.os.Parcelable;

import com.ruideraj.secretelephant.contacts.Contact;

import java.util.ArrayList;

/**
 * Class containing all the data needed for a gift exchange.
 */
public class MatchExchange implements Parcelable {

    private ArrayList<Contact> mContacts;
    private int[] mMatches;
    private int mMode;

    public MatchExchange(ArrayList<Contact> contacts, int[] matches, int mode) {
        mContacts = contacts;
        mMatches = matches;
        mMode = mode;
    }

    public ArrayList<Contact> getContacts() {
        return mContacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.mContacts = contacts;
    }

    public int[] getMatches() {
        return mMatches;
    }

    public void setMatches(int[] matches) {
        this.mMatches = matches;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mContacts);
        dest.writeIntArray(this.mMatches);
        dest.writeInt(this.mMode);
    }

    public MatchExchange() {
    }

    protected MatchExchange(Parcel in) {
        this.mContacts = in.createTypedArrayList(Contact.CREATOR);
        this.mMatches = in.createIntArray();
        this.mMode = in.readInt();
    }

    public static final Parcelable.Creator<MatchExchange> CREATOR = new Parcelable.Creator<MatchExchange>() {
        @Override
        public MatchExchange createFromParcel(Parcel source) {
            return new MatchExchange(source);
        }

        @Override
        public MatchExchange[] newArray(int size) {
            return new MatchExchange[size];
        }
    };
}
