package com.ruideraj.secretelephant.injection

import android.content.Context
import android.telephony.SmsManager
import com.ruideraj.secretelephant.PropertiesReader
import com.ruideraj.secretelephant.send.EmailSender
import com.ruideraj.secretelephant.send.SmsSender
import dagger.Module
import dagger.Provides

@Module
class SendModule {

    @Provides
    fun providesSmsSender() = SmsSender(SmsManager.getDefault())

    @Provides
    fun providesEmailSender(context: Context) = EmailSender(context)

    @Provides
    fun providesPropertiesReader(context: Context) = PropertiesReader(context)

}