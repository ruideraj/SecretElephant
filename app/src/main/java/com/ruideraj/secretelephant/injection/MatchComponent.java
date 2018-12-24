package com.ruideraj.secretelephant.injection;

import com.ruideraj.secretelephant.Runner;

import dagger.Subcomponent;

@Subcomponent
public interface MatchComponent {

    Runner runner();

}
