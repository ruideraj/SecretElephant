package com.ruideraj.secretelephant.send

interface Sender {
    suspend fun sendMessage(message: Message)
}