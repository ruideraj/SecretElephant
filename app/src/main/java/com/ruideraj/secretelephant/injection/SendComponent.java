package com.ruideraj.secretelephant.injection;

import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.send.SendRepository;
import com.ruideraj.secretelephant.send.SendViewModel;

import dagger.Subcomponent;

@Subcomponent(modules = {SendModule.class, ContextModule.class})
public interface SendComponent {

    SendRepository sendRepository();
    AccountManager accountManager();

    SendViewModel sendViewModel();
}
