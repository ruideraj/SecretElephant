package com.ruideraj.secretelephant.send;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;
import com.ruideraj.secretelephant.BuildConfig;
import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.PropertiesReader;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.SingleLiveEvent;
import com.ruideraj.secretelephant.contacts.Contact;
import com.ruideraj.secretelephant.match.MatchExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendRepository {

    private static volatile SendRepository INSTANCE;
    private static final Object sLock = new Object();

    private Context mContext;

    private SendRunner mRunner;

    private SmsSender mSmsSender;
    private EmailSender mEmailSender;

    private final MutableLiveData<List<SendInvite>> invites = new MutableLiveData<>();
    private final SingleLiveEvent<int[]> lastUpdatedPosition = new SingleLiveEvent<>();

    public static SendRepository getInstance(Context context, SendRunner runner,
                                             SmsSender smsSender, EmailSender emailSender) {
        if(INSTANCE == null) {
            synchronized(sLock) {
                if(INSTANCE == null) {
                    INSTANCE = new SendRepository(context, runner, smsSender, emailSender);
                }
            }
        }

        return INSTANCE;
    }

    public SendRepository(Context context, SendRunner runner,
                  SmsSender smsSender, EmailSender emailSender) {
        mContext = context;
        mRunner = runner;
        mSmsSender = smsSender;
        mEmailSender = emailSender;
    }

    MutableLiveData<List<SendInvite>> getInvites() {
        return invites;
    }

    SingleLiveEvent<int[]> getLastUpdatedPosition() {
        return lastUpdatedPosition;
    }

    private void setInvites(List<SendInvite> list) {
        invites.setValue(list);
    }

    void setEmailAccount(String emailAccount) {
        mEmailSender.setEmailAccount(emailAccount);
    }

    private void setStatus(int position, int status, Exception e) {
        List<SendInvite> list = invites.getValue();

        if(list != null) {
            SendInvite invite = list.get(position);
            invite.setStatus(status);
            invite.setException(e);

            int[] update = new int[2];
            update[0] = position;
            update[1] = status;

            lastUpdatedPosition.setValue(update);
        }
    }

    public void send(MatchExchange exchange) {
        SendListTask task = new SendListTask(exchange);
        mRunner.runBackground(task);
    }

    public void resend(int position) {
        List<SendInvite> list = invites.getValue();
        if(list != null && position < list.size()) {
            int status = SendInvite.IN_PROGRESS;
            SendInvite invite = list.get(position);
            invite.setStatus(status);
            invite.setException(null);

            setStatus(position, status, null);

            SendTask task = new SendTask(position, invite);
            mRunner.runBackground(task);
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

    private class SendListTask implements Runnable {
        private MatchExchange mExchange;

        SendListTask(MatchExchange exchange) {
            mExchange = exchange;
        }

        @Override
        public void run() {
            // Build Invite objects.
            /* TODO
             *  Can store them in local database to allow retries when an error occurs or the
             *  app is stopped/placed in background.
             */
            final List<SendInvite> list = buildInvites(mExchange.getContacts(),
                    mExchange.getMatches(), mExchange.getMode());

            // Set newly created list of invites on the UI thread.
            mRunner.runUi(() -> setInvites(list));

            // Post each invite as a task for the ExecutorService.
            for(int i = 0; i < list.size(); i++) {
                SendTask task = new SendTask(i, list.get(i));
                mRunner.runBackground(task);
            }
        }
    }

    private class SendTask implements Runnable {

        private int mPosition;
        private SendInvite mInvite;

        SendTask(int position, SendInvite invite) {
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
                    mSmsSender.sendMessage(destination, mInvite.getMessage());
                }
                catch(IllegalArgumentException e) {
                    if(BuildConfig.DEBUG) e.printStackTrace();
                    exception = e;
                }
            }
            else if(mInvite.getContact().getType() == Contact.TYPE_EMAIL) {
                if(mEmailSender.isEmailAccountPresent()) {
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
                        mEmailSender.sendEmail("me", message);
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
                    // If no account name/email was set,
                    // the user may have revoked account permission.
                    // Create a generic Exception to indicate that we couldn't send the email.
                    exception = new Exception(mContext.getString(R.string.send_error_no_gmail));
                }
            }

            // Set the item status and update the UI.
            int status = exception == null ? SendInvite.SENT : SendInvite.ERROR;
            ItemUpdateTask task = new ItemUpdateTask(mPosition, status, exception);
            mRunner.runUi(task);
        }
    }

    private class ItemUpdateTask implements Runnable {

        private int mPosition;
        private int mStatus;
        private Exception mException;

        ItemUpdateTask(int position, int status, Exception e) {
            mPosition = position;
            mStatus = status;
            mException = e;
        }

        @Override
        public void run() {
            setStatus(mPosition, mStatus, mException);
        }
    }
}
