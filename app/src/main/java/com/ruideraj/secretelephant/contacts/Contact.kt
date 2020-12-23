package com.ruideraj.secretelephant.contacts

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact (val name: String,
                    val type: Type,
                    val data: String,
                    val avatarUri: String? = null) : Parcelable {

    enum class Type { PHONE, EMAIL }

}