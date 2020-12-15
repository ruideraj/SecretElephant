package com.ruideraj.secretelephant.injection

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import com.ruideraj.secretelephant.Runner
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
class RunnerModule {

    @Provides
    @Singleton
    fun providesRunner(executor: Executor, handler: Handler) = Runner(executor, handler)

    @Provides
    @Singleton
    fun providesExecutor(): Executor = AsyncTask.THREAD_POOL_EXECUTOR

    @Provides
    @Singleton
    fun providesHandler() = Handler(Looper.getMainLooper())

}