package com.ruideraj.secretelephant.injection;

import android.content.Context;
import android.telephony.SmsManager;

import com.ruideraj.secretelephant.PropertiesReader;
import com.ruideraj.secretelephant.Runner;
import com.ruideraj.secretelephant.send.EmailSender;
import com.ruideraj.secretelephant.send.SendRepository;
import com.ruideraj.secretelephant.send.SmsSender;

import dagger.Module;
import dagger.Provides;

@Module
public class SendModule {

    @Provides
    static SmsSender providesSmsSender() {
        return new SmsSender(SmsManager.getDefault());
    }

    @Provides
    static EmailSender providesEmailSender(Context context) {
        return new EmailSender(context);
    }

    @Provides
    static PropertiesReader providesPropertiesReader(Context context) {
        return new PropertiesReader(context);
    }

}
