package com.ruideraj.secretelephant

import android.content.Context
import java.io.IOException
import java.util.*

class PropertiesReader(private val context : Context) {

    fun getProperty(key : String) : String {
        val properties = Properties()
        val assetManager = context.assets

        try {
            val inputStream = assetManager.open(context.getString(R.string.test_filename))
            properties.load(inputStream)
        }
        catch(io : IOException) {
            io.printStackTrace()
        }

        return properties.getProperty(key)
    }

}