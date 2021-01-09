package com.ruideraj.secretelephant.send

import com.ruideraj.secretelephant.contacts.Contact

data class Message(val contact: Contact, val message: String) {
    var status = Status.IN_PROGRESS
    var exception: Exception? = null

    enum class Status { IN_PROGRESS, ERROR, SENT }
}