package com.ruideraj.secretelephant.send;

import android.content.Context;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.ruideraj.secretelephant.R;

import java.io.IOException;
import java.util.Arrays;

public class EmailSender {

    private static final String[] SCOPES = { GmailScopes.GMAIL_SEND };
    private static final int BACKOFF_MAX_ELAPSED = 8000;

    private static volatile EmailSender INSTANCE;
    private static final Object sLock = new Object();

    private Context mContext;
    private Gmail mGmail;

    public static EmailSender getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized(sLock) {
                if(INSTANCE == null) {
                    INSTANCE = new EmailSender(context);
                }
            }
        }

        return INSTANCE;
    }

    public EmailSender(Context context) {
        mContext = context;
    }

    public boolean isEmailAccountPresent() {
        return mGmail != null;
    }

    public void setEmailAccount(String emailAccount) {
        if(!TextUtils.isEmpty(emailAccount)) {
            ExponentialBackOff backOff = new ExponentialBackOff.Builder()
                    .setMaxElapsedTimeMillis(BACKOFF_MAX_ELAPSED).build();

            GoogleAccountCredential credential = GoogleAccountCredential
                    .usingOAuth2(mContext, Arrays.asList(SCOPES))
                    .setBackOff(backOff);
            credential.setSelectedAccountName(emailAccount);
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mGmail = new Gmail.Builder(transport, jsonFactory, credential)
                    .setApplicationName(mContext.getString(R.string.app_name)).build();
        }
        else {
            mGmail = null;
        }
    }

    public void sendEmail(String accountName, Message message) throws IOException {
        mGmail.users().messages().send(accountName, message).execute();
    }
}
