package com.ruideraj.secretelephant

import android.content.Context
import java.io.IOException
import java.util.*
import javax.inject.Inject

interface PropertiesReader {
    fun getProperty(stringId: Int): String
    fun getProperty(key: String): String
}

class PropertiesReaderImpl @Inject constructor(private val context : Context) : PropertiesReader {

    override fun getProperty(stringId: Int) = getProperty(context.getString(stringId))

    override fun getProperty(key : String) : String {
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