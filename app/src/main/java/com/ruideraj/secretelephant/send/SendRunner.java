package com.ruideraj.secretelephant.send;

import android.os.Handler;

import java.util.concurrent.Executor;

public class SendRunner {

    private static volatile SendRunner INSTANCE;
    private static final Object sLock = new Object();

    private Executor mExecutor;
    private Handler mHandler;

    public static SendRunner getInstance(Executor executor, Handler handler) {
        if(INSTANCE == null) {
            synchronized(sLock) {
                if(INSTANCE == null) {
                    INSTANCE = new SendRunner(executor, handler);
                }
            }
        }

        return INSTANCE;
    }

    public SendRunner(Executor executor, Handler handler) {
        mExecutor = executor;
        mHandler = handler;
    }

    public void runBackground(Runnable runnable) {
        mExecutor.execute(runnable);
    }

    public void runUi(Runnable runnable) {
        mHandler.post(runnable);
    }
}
