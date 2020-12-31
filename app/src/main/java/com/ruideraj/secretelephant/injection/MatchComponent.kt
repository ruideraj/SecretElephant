package com.ruideraj.secretelephant.injection

import com.ruideraj.secretelephant.match.MatchViewModel
import dagger.Subcomponent

@Subcomponent(modules = [MatchModule::class, ActivityModule::class])
interface MatchComponent {

    fun matchViewModel(): MatchViewModel

}