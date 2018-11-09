package com.ruideraj.secretelephant.send;

import android.telephony.SmsManager;

public class SmsSender {

    private static volatile SmsSender INSTANCE;
    private static final Object sLock = new Object();

    private SmsManager mSmsManager;

    public static SmsSender getInstance(SmsManager smsManager) {
        if(INSTANCE == null) {
            synchronized(sLock) {
                if(INSTANCE == null) {
                    INSTANCE = new SmsSender(smsManager);
                }
            }
        }

        return INSTANCE;
    }

    public SmsSender(SmsManager smsManager) {
        mSmsManager = smsManager;
    }

    public void sendMessage(String recipient, String message) throws IllegalArgumentException {
        mSmsManager.sendTextMessage(recipient,
                null, message, null, null);
    }
}
