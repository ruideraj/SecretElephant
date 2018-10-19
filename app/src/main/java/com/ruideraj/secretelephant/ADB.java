package com.ruideraj.secretelephant;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * App Debug class for logging.
 */
public class ADB {

    public static void toast(Context context, String text, int duration) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, text, duration).show();
        }
    }

    public static void println(String text) {
        if (BuildConfig.DEBUG) {
            System.out.println(text);
        }
    }

    public static void v(String tag, String text) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, text);
        }
    }

    public static void i(String tag, String text) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, text);
        }
    }

    public static void d(String tag, String text) {
        if (BuildConfig.DEBUG) {
            if (text == null)
                text = "no message";
            Log.d(tag, text);
        }
    }

    public static void d(String tag, String text, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String text, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, text, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String text, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, text, tr);
        }
    }

}
