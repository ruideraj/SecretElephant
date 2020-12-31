package com.ruideraj.secretelephant.injection

import com.ruideraj.secretelephant.match.Matchmaker
import com.ruideraj.secretelephant.match.MatchmakerImpl
import dagger.Module
import dagger.Provides

@Module
class MatchModule {

    @Provides
    fun providesMatchmaker(matchmakerImpl: MatchmakerImpl): Matchmaker = matchmakerImpl

}