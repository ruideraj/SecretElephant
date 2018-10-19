package com.ruideraj.secretelephant.contacts;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contact data object.
 */
public class Contact implements Parcelable {

    public static final int TYPE_PHONE = 0;
    public static final int TYPE_EMAIL = 1;

    private String name;
    private int type;
    private String data;
    private Uri avatarUri;

    private boolean selected;

    public Contact(String name, int type, String data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.type);
        dest.writeString(this.data);
        dest.writeParcelable(this.avatarUri, flags);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.type = in.readInt();
        this.data = in.readString();
        this.avatarUri = in.readParcelable(Uri.class.getClassLoader());
        this.selected = in.readByte() != 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
