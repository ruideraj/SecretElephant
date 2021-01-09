package com.ruideraj.secretelephant.match

import android.os.Parcelable
import com.ruideraj.secretelephant.Mode
import com.ruideraj.secretelephant.contacts.Contact
import kotlinx.parcelize.Parcelize

@Parcelize
data class MatchExchange(val contacts: List<Contact>, val matches: IntArray, val mode: Mode)
    : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatchExchange

        if (contacts != other.contacts) return false
        if (!matches.contentEquals(other.matches)) return false
        if (mode != other.mode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contacts.hashCode()
        result = 31 * result + matches.contentHashCode()
        result = 31 * result + mode.hashCode()
        return result
    }


}