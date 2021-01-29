package com.ruideraj.secretelephant.contacts

data class  ContactUpdate(val type: Contact.Type, val added: Boolean, val selectedPosition: Int, val listPosition: Int)