package com.ruideraj.secretelephant.main;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AccountManager mAccountManager;

    @Mock
    private GoogleSignInAccount mAccount;

    private MainViewModel mMainViewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mMainViewModel = new MainViewModel(mAccountManager);
    }

    @Test
    public void startWithAccount() {
        when(mAccountManager.getAccount()).thenReturn(mAccount);

        mMainViewModel.start();

        assertNotNull(mMainViewModel.signedIn.getValue());
        assertTrue(mMainViewModel.signedIn.getValue());
    }

    @Test
    public void startNoAccount() {
        when(mAccountManager.getAccount()).thenReturn(null);

        mMainViewModel.start();

        assertNotNull(mMainViewModel.signedIn.getValue());
        assertFalse(mMainViewModel.signedIn.getValue());
    }

    @Test
    public void signOut() {
        mMainViewModel.signOut();
        verify(mAccountManager, times(1))
                .signOut(ArgumentMatchers.any(AccountManager.AccountListener.class));
    }

    @Test
    public void signOutSuccess() {
        mMainViewModel.onSignOutSuccess();

        assertNotNull(mMainViewModel.signedIn.getValue());
        assertFalse(mMainViewModel.signedIn.getValue());

        assertNotNull(mMainViewModel.signOutMessage.getValue());
        assertEquals(R.string.main_menu_signed_out, (int) mMainViewModel.signOutMessage.getValue());
    }

    @Test
    public void signOutFailure() {
        mMainViewModel.onSignOutFailure();

        assertNotNull(mMainViewModel.signOutMessage.getValue());
        assertEquals(R.string.main_menu_sign_out_failed, (int) mMainViewModel.signOutMessage.getValue());
    }
}