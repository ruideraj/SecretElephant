package com.ruideraj.secretelephant;

import android.os.Handler;

import java.util.concurrent.Executor;

public class Runner {

    private Executor mExecutor;
    private Handler mHandler;

    public Runner(Executor executor, Handler handler) {
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
