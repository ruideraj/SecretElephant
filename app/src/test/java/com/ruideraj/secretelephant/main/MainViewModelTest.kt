package com.ruideraj.secretelephant.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.whenever
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.R
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var accountManager: AccountManager

    @Mock
    private lateinit var account: GoogleSignInAccount

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = MainViewModel(accountManager)
    }

    @Test
    fun mainViewModel_start_signedInWhenAccountPresent() {
        whenever(accountManager.getAccount()).thenReturn(account)

        viewModel.start()

        assertThat(viewModel.signedIn.value, equalTo(true))
    }

    @Test
    fun mainViewModel_start_signedOutWhenAccountNotPresent() {
        viewModel.start()
        assertThat(viewModel.signedIn.value, equalTo(false))
    }

    @Test
    fun mainViewModel_signOut_signOutSucceeds_signedOutWithMessage() {
        val listenerCaptor = argumentCaptor<AccountManager.AccountListener>()

        viewModel.signOut()

        verify(accountManager).signOut(listenerCaptor.capture())

        listenerCaptor.firstValue.apply {
            onSignOutSuccess()
        }

        assertThat(viewModel.signedIn.value, equalTo(false))
        assertThat(viewModel.signOutMessage.value, equalTo(R.string.main_menu_signed_out))
    }
    
    @Test
    fun mainViewModel_signOut_signOutFails_showsSignOutFailMessage() {
        val listenerCaptor = argumentCaptor<AccountManager.AccountListener>()

        viewModel.signOut()

        verify(accountManager).signOut(listenerCaptor.capture())

        listenerCaptor.firstValue.apply {
            onSignOutFailure()
        }

        assertThat(viewModel.signOutMessage.value, equalTo(R.string.main_menu_sign_out_failed))
    }
}