package com.ruideraj.secretelephant.send

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ruideraj.secretelephant.Mode
import com.ruideraj.secretelephant.PropertiesReader
import com.ruideraj.secretelephant.SingleLiveEvent
import com.ruideraj.secretelephant.contacts.Contact
import com.ruideraj.secretelephant.match.MatchExchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.mail.MessagingException
import kotlin.random.Random

interface SendRepository {
    val messages: LiveData<List<Message>>
    val lastUpdatedPosition: LiveData<Update>

    fun setEmailAccount(email: String?)

    suspend fun send(exchange: MatchExchange)
    suspend fun resend(position: Int)

    data class Update(val position: Int, val status: Message.Status)
}

class SendRepositoryImpl @Inject constructor(private val context: Context,
                                             @Named("SmsSender") private val smsSender: Sender,
                                             private val emailSender: EmailSender,
                                             private val propertiesReader: PropertiesReader) : SendRepository {

    override val messages: LiveData<List<Message>>
        get() = messagesData
    private val messagesData = MutableLiveData<List<Message>>()

    override val lastUpdatedPosition: LiveData<SendRepository.Update>
        get() = lastUpdatedPositionData
    private val lastUpdatedPositionData = SingleLiveEvent<SendRepository.Update>()

    override fun setEmailAccount(email: String?) {
        emailSender.setEmailAccount(email)
    }

    override suspend fun send(exchange: MatchExchange) {
        // Build Message objects.
        /* TODO
         *  Can store them in local database to allow retries when an error occurs or the
         *  app is stopped/placed in background.
         */
        val messages = buildMessages(exchange.contacts, exchange.matches, exchange.mode)

        withContext(Dispatchers.Main) {
            messagesData.value = messages
        }

        withContext(Dispatchers.IO) {
            messages.forEachIndexed { index, message ->
                launch {
                    sendMessage(index, message)
                }
            }
        }
    }

    override suspend fun resend(position: Int) {
        val list = messages.value
        if (list != null && position < list.size) {
            val message = list[position]
            setMessageStatus(position, message, Message.Status.IN_PROGRESS, null)
            withContext(Dispatchers.IO) { launch { sendMessage(position, message) } }
        }
    }

    private fun buildMessages(contacts: List<Contact>, matches: IntArray, mode: Mode): List<Message> {
        val messages = mutableListOf<Message>()

        contacts.forEachIndexed { index, contact ->
            // TODO Move the text logic into a helper class that pulls the strings from resources.
            val message = if (mode == Mode.SANTA) {
                "${contact.name}, you will be giving to ${contacts[matches[index]].name}!"
            } else {
                "${contact.name}, you number is ${matches[index] + 1}!"
            }

            messages.add(Message(contact, message))
        }

        return messages
    }

    private suspend fun sendMessage(index: Int, message: Message) {
        // TODO REMOVE LATER
        // Add a random amount of seconds to simulate slower network conditions.
        val delayTime = 3 + Random.Default.nextInt(5)
        delay(delayTime * 1000L)

        var exception: Exception? = null
        if (message.contact.type == Contact.Type.PHONE) {
            try {
                smsSender.sendMessage(message)
            } catch (e: IllegalArgumentException) {
                exception = e
            }
        } else if (message.contact.type == Contact.Type.EMAIL) {
            if (emailSender.isEmailAccountPresent()) {
                try {
                    emailSender.sendMessage(message)
                } catch (e: IllegalStateException) {
                    exception = e
                } catch (e: MessagingException) {
                    exception = e
                } catch (e: IOException) {
                    exception = e
                }
            } else {
                // If no account name/email was set,
                // the user may have revoked account permission.
                // Create a generic Exception to indicate that we couldn't send the email.
                exception = Exception("No email account set")
            }
        }

        val status = if (exception != null) Message.Status.ERROR else Message.Status.SENT

        setMessageStatus(index, message, status, exception)
    }

    private suspend fun setMessageStatus(position: Int, message: Message, status: Message.Status, exception: Exception?) {
        withContext(Dispatchers.Main) {  // Modify Message objects only on main thread
            message.status = status
            message.exception = exception

            lastUpdatedPositionData.value = SendRepository.Update(position, status)
        }
    }
}