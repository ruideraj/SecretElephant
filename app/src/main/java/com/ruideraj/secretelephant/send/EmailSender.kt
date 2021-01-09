package com.ruideraj.secretelephant.send

import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
import com.ruideraj.secretelephant.BuildConfig
import com.ruideraj.secretelephant.PropertiesReader
import com.ruideraj.secretelephant.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

interface EmailSender : Sender {
    fun isEmailAccountPresent(): Boolean
    fun setEmailAccount(email: String?)
}

class EmailSenderImpl @Inject constructor(private val context: Context,
                                          private val propertiesReader: PropertiesReader) : EmailSender {

    companion object {
        private val SCOPES = listOf(GmailScopes.GMAIL_SEND)
        private const val BACKOFF_MAX_ELAPSED = 8000
    }

    private var gmailAccount: Gmail? = null

    override fun isEmailAccountPresent() = gmailAccount != null

    override fun setEmailAccount(email: String?) = if (!email.isNullOrBlank()) {
        val backoff = ExponentialBackOff.Builder()
                .setMaxElapsedTimeMillis(BACKOFF_MAX_ELAPSED).build()
        val credential = GoogleAccountCredential
                .usingOAuth2(context, SCOPES).setBackOff(backoff).setSelectedAccountName(email)
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        gmailAccount = Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName(context.getString(R.string.app_name)).build()
    } else {
        gmailAccount = null
    }

    override suspend fun sendMessage(message: com.ruideraj.secretelephant.send.Message) {
        val emailRecipient: String = if (BuildConfig.DEBUG) {
            propertiesReader.getProperty(R.string.test_send_key_email)
        } else {
            ""  // TODO Use actual recipient email
        }
        val gmailMessage = createGmailMessage(emailRecipient, message.message)

        @Suppress("BlockingMethodInNonBlockingContext")
        // Suppressing since we're using IO dispatcher which is meant for blocking calls.
        withContext(Dispatchers.IO) {
            val gmail = gmailAccount
            if (gmail != null) {
                gmail.users().messages().send(emailRecipient, gmailMessage).execute()
            } else {
                throw IllegalStateException("No email set")
            }
        }
    }

    private fun createGmailMessage(emailRecipient: String, messageContent: String): Message {
        // Send email through Gmail API.
        try {
            val props = Properties()
            val session = Session.getDefaultInstance(props)
            val email = MimeMessage(session).apply {
                addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(emailRecipient))
                subject = context.getString(R.string.app_name)
                setText(messageContent)
            }
            val buffer = ByteArrayOutputStream()
            email.writeTo(buffer)
            val encodedEmail = Base64.encodeBase64URLSafeString(buffer.toByteArray())
            val message = Message()
            message.raw = encodedEmail

            return message
        } catch (me: MessagingException) {
            if (BuildConfig.DEBUG) me.printStackTrace()
            throw me
        } catch (io: IOException) {
            if (BuildConfig.DEBUG) io.printStackTrace()
            throw io
        }
    }
}

