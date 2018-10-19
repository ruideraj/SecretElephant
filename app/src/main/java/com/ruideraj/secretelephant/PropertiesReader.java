package com.ruideraj.secretelephant;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public static String getProperty(Context context, String key) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(context.getString(R.string.test_filename));
            properties.load(inputStream);
        }
        catch(IOException io) {
            io.printStackTrace();
        }

        return properties.getProperty(key);
    }

}
