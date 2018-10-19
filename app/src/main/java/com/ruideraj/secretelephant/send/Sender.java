package com.ruideraj.secretelephant.send;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.ruideraj.secretelephant.BuildConfig;
import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.PropertiesReader;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.contacts.Contact;
import com.ruideraj.secretelephant.match.MatchExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Sender {

    private static final String[] SCOPES = { GmailScopes.GMAIL_SEND };
    private static final int THREADS = 5;

    private List<SendService.SendListener> mListeners = new ArrayList<>();

    private Context mContext;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(THREADS);
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private SmsManager mSmsManager;
    private String mAccountName = null;
    private Gmail mGmail = null;

    private List<SendInvite> mInvites;

    public Sender(Context context) {
        mContext = context;
    }

    public void send(MatchExchange exchange) {
        SendListTask task = new SendListTask(exchange);
        mExecutorService.submit(task);
    }

    public void resend(int position) {
        if(mInvites != null && position < mInvites.size()) {
            SendInvite invite = mInvites.get(position);
            invite.setStatus(SendInvite.IN_PROGRESS);
            invite.setException(null);

            for(SendService.SendListener listener : mListeners) {
                listener.sendItemUpdated(position);
            }

            SendTask task = new SendTask(position, invite);
            mExecutorService.submit(task);
        }
    }

    private List<SendInvite> buildInvites(List<Contact> contacts, int[] matches, int mode) {
        List<SendInvite> invites = new ArrayList<>(contacts.size());

        for(int i = 0, size = contacts.size(); i < size; i++) {
            Contact contact = contacts.get(i);

            StringBuilder sb = new StringBuilder();
            sb.append(contact.getName()).append(", ");
            if(mode == Constants.MODE_SANTA) {
                sb.append(mContext.getString(R.string.send_santa));
                Contact recipient = contacts.get(matches[i]);
                sb.append(' ').append(recipient.getName());

            }
            else if(mode == Constants.MODE_ELEPHANT) {
                sb.append(mContext.getString(R.string.send_elephant));
                sb.append(' ').append(matches[i] + 1); // + 1 for 0-based index.
            }
            sb.append('!');

            invites.add(new SendInvite(contact, sb.toString()));
        }

        return invites;
    }

    public interface SendListener {
        void sendListCreated(List<SendInvite> invites);
        void sendItemUpdated(int position);
        void sendCompleted();
    }

    private class SendListTask implements Runnable {
        private MatchExchange mExchange;

        public SendListTask(MatchExchange exchange) {
            mExchange = exchange;
        }

        @Override
        public void run() {
            // TODO
            // Could pass in flags where we only initialize Sms or Gmail if
            // phone contacts or email contacts are present.

            mSmsManager = SmsManager.getDefault();

            GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(mContext);
            if(googleAccount != null) {
                mAccountName = googleAccount.getEmail();
            }

            if(!TextUtils.isEmpty(mAccountName)) {
                GoogleAccountCredential credential = GoogleAccountCredential
                        .usingOAuth2(mContext, Arrays.asList(SCOPES))
                        .setBackOff(new ExponentialBackOff());
                credential.setSelectedAccountName(mAccountName);
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                mGmail = new Gmail.Builder(transport, jsonFactory, credential)
                        .setApplicationName(mContext.getString(R.string.app_name)).build();
            }
            else {
                // TODO
                // Need to handle the case where the user for some reason revokes
                // access to their Google account right before sending the invites.
            }


            // Build Invite objects.
            /* TODO
             *  Can store them in local database to allow retries when an error occurs or the
             *  app is stopped/placed in background.
             */
            final List<SendInvite> invites = buildInvites(mExchange.getContacts(),
                    mExchange.getMatches(), mExchange.getMode());

            // Set newly created list of invites on the UI thread.
            mHandler.post(() -> {
                mInvites = invites;
                for(SendService.SendListener listener : mListeners) {
                    listener.sendListCreated(mInvites);
                }
            });

            // Post each invite as a task for the ExecutorService.
            for(int i = 0; i < invites.size(); i++) {
                SendTask task = new SendTask(i, invites.get(i));
                mExecutorService.submit(task);
            }
        }
    }

    private class SendTask implements Runnable {

        private int mPosition;
        private SendInvite mInvite;

        public SendTask(int position, SendInvite invite) {
            mPosition = position;
            mInvite = invite;
        }

        @Override
        public void run() {
            // TODO REMOVE LATER
            // Add a random amount of seconds (3 - 8) to simulate slower network conditions.
            Random random = new Random();
            int delay =  3 + random.nextInt(5);
            try {
                Thread.sleep(delay * 1000);
            }
            catch(InterruptedException e) {
                // Do nothing lol
            }

            Exception exception = null;
            if(mInvite.getContact().getType() == Contact.TYPE_PHONE) {
                String destination = null;
                if(BuildConfig.DEBUG) {
                    destination = PropertiesReader.getProperty(mContext,
                            mContext.getString(R.string.test_send_key_phone));
                }
                else {
                    // TODO Add logic to use real phone number.
                }
                try {
                    mSmsManager.sendTextMessage(destination,
                            null, mInvite.getMessage(), null, null);
                }
                catch(IllegalArgumentException e) {
                    if(BuildConfig.DEBUG) e.printStackTrace();
                    exception = e;
                }
            }
            else if(mInvite.getContact().getType() == Contact.TYPE_EMAIL) {
                if(mGmail != null) {
                    // Build email object.
                    Properties props = new Properties();
                    Session session = Session.getDefaultInstance(props);
                    MimeMessage email = new MimeMessage(session);

                    // Send email through Gmail API.
                    try {
                        String recipient = null;
                        if(BuildConfig.DEBUG) {
                            recipient = PropertiesReader.getProperty(mContext,
                                    mContext.getString(R.string.test_send_key_email));
                        }
                        else {
                            // TODO Add logic to use real email
                        }
                        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
                        email.setSubject(mContext.getString(R.string.app_name));
                        email.setText(mInvite.getMessage());

                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        email.writeTo(buffer);
                        byte[] bytes = buffer.toByteArray();
                        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
                        Message message = new Message();
                        message.setRaw(encodedEmail);
                        mGmail.users().messages().send(mAccountName, message).execute();
                    }
                    catch(MessagingException me) {
                        if(BuildConfig.DEBUG) me.printStackTrace();
                        exception = me;
                    }
                    catch(IOException io) {
                        if(BuildConfig.DEBUG) io.printStackTrace();
                        exception = io;
                    }
                }
                else {
                    // If Gmail object is null, no account name/email was set,
                    // meaning the user may have revoked account permission.
                    // Create a generic Exception to indicate that we couldn't send the email.
                    exception = new Exception(mContext.getString(R.string.send_error_no_gmail));
                }
            }

            // Set the item status and update the UI.
            int status = exception == null ? SendInvite.SENT : SendInvite.ERROR;
            ItemUpdateTask task = new ItemUpdateTask(mPosition, mInvite, status, exception);
            mHandler.post(task);
        }
    }

    private class ItemUpdateTask implements Runnable {

        private int mPosition;
        private SendInvite mInvite;
        private int mStatus;
        private Exception mException;

        public ItemUpdateTask(int position, SendInvite invite, int status, Exception e) {
            mPosition = position;
            mInvite = invite;
            mStatus = status;
            mException = e;
        }

        @Override
        public void run() {
            mInvite.setStatus(mStatus);
            mInvite.setException(mException);

            for(SendService.SendListener listener : mListeners) {
                listener.sendItemUpdated(mPosition);
            }
        }
    }

}
