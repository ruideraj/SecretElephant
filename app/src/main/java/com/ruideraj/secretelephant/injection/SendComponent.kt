package com.ruideraj.secretelephant.injection

import com.ruideraj.secretelephant.send.SendViewModel
import dagger.Subcomponent

@Subcomponent(modules = [SendModule::class, ActivityModule::class])
interface SendComponent {
    fun sendViewModel(): SendViewModel
}