package com.ruideraj.secretelephant

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * App Debug class for logging.
 */
object AppLog {
    @JvmStatic fun toast(context: Context, text: String, duration: Int) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, text, duration).show()
        }
    }

    @JvmStatic fun println(text: String) {
        if (BuildConfig.DEBUG) {
            println(text)
        }
    }

    @JvmStatic fun v(tag: String, text: String) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, text)
        }
    }

    @JvmStatic fun i(tag: String, text: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, text)
        }
    }

    @JvmStatic fun d(tag: String, text: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text)
        }
    }

    @JvmStatic fun d(tag: String, text: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text, tr)
        }
    }

    @JvmStatic fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg)
        }
    }

    @JvmStatic fun w(tag: String, text: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, text, tr)
        }
    }

    @JvmStatic fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
    }

    @JvmStatic fun e(tag: String, text: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, text, tr)
        }
    }
}