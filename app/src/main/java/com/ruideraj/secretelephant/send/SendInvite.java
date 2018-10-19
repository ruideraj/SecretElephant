package com.ruideraj.secretelephant.send;

import com.ruideraj.secretelephant.contacts.Contact;

public class SendInvite {

    public static final int IN_PROGRESS = 0;
    public static final int ERROR = 1;
    public static final int SENT = 2;

    private Contact mContact;
    private String mMessage;
    private int mStatus;
    private Exception mException;

    public SendInvite(Contact contact, String message) {
        mContact = contact;
        mMessage = message;
        mStatus = IN_PROGRESS;
    }

    public Contact getContact() {
        return mContact;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception e) {
        mException = e;
    }

}
