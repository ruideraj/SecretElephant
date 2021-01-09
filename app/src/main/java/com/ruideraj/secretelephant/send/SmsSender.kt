package com.ruideraj.secretelephant.send

import android.telephony.SmsManager
import com.ruideraj.secretelephant.BuildConfig
import com.ruideraj.secretelephant.PropertiesReader
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.contacts.Contact
import javax.inject.Inject

class SmsSender @Inject constructor(private val smsManager: SmsManager,
                                    private val propertiesReader: PropertiesReader): Sender {
    
    override suspend fun sendMessage(message: Message) {
        val contact = message.contact
        if (contact.type != Contact.Type.PHONE) throw IllegalArgumentException("Contact must be a phone number")

        val destination = if (BuildConfig.DEBUG) {
            propertiesReader.getProperty(R.string.test_send_key_phone)
        } else {
            ""  // TODO Add logic to use real phone number.
        }

        smsManager.sendTextMessage(destination, null, message.message, null, null)
    }
}