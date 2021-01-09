package com.ruideraj.secretelephant.injection

import android.telephony.SmsManager
import com.ruideraj.secretelephant.PropertiesReader
import com.ruideraj.secretelephant.PropertiesReaderImpl
import com.ruideraj.secretelephant.send.*
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class SendModule {

    @Provides
    fun providesSendRepository(sendRepositoryImpl: SendRepositoryImpl): SendRepository = sendRepositoryImpl

    @Provides
    @Named("SmsSender")
    fun providesSmsSender(propertiesReader: PropertiesReader): Sender = SmsSender(SmsManager.getDefault(),
                                                                                  propertiesReader)

    @Provides
    fun providesEmailSender(emailSenderImpl: EmailSenderImpl): EmailSender = emailSenderImpl

    @Provides
    fun providesPropertiesReader(propertiesReaderImpl: PropertiesReaderImpl): PropertiesReader = propertiesReaderImpl

}