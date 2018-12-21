package com.ruideraj.secretelephant.injection;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.ruideraj.secretelephant.Runner;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RunnerModule {

    @Provides
    @Singleton
    static Runner providesRunner(Executor executor, Handler handler) {
        return new Runner(executor, handler);
    }

    @Provides
    @Singleton
    static Executor providesExecutor() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }

    @Provides
    @Singleton
    static Handler providesHandler() {
        return new Handler(Looper.getMainLooper());
    }

}
