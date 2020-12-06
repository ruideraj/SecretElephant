package com.ruideraj.secretelephant

import android.os.Handler
import java.util.concurrent.Executor

class Runner constructor(private val executor: Executor, private val handler: Handler) {

    fun runBackground(runnable: Runnable) = executor.execute(runnable)
    fun runUi(runnable: Runnable) = handler.post(runnable)

}