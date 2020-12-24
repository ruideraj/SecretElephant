package com.ruideraj.secretelephant.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.R
import io.mockk.*
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private var accountManager: AccountManager = mockk()

    private var account: GoogleSignInAccount = mockk()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(accountManager)
    }

    @Test
    fun mainViewModel_start_signedInWhenAccountPresent() {
        every { accountManager.getAccount() } returns account

        viewModel.start()

        assertThat(viewModel.signedIn.value, equalTo(true))
    }

    @Test
    fun mainViewModel_start_signedOutWhenAccountNotPresent() {
        every { accountManager.getAccount() } returns null

        viewModel.start()
        assertThat(viewModel.signedIn.value, equalTo(false))
    }

    @Test
    fun mainViewModel_signOut_signOutSucceeds_signedOutWithMessage() {
        val listenerSlot = slot<AccountManager.AccountListener>()
        every { accountManager.signOut(capture(listenerSlot)) } just Runs

        viewModel.signOut()

        verify { accountManager.signOut(any()) }

        listenerSlot.captured.onSignOutSuccess()

        assertThat(viewModel.signedIn.value, equalTo(false))
        assertThat(viewModel.signOutMessage.value, equalTo(R.string.main_menu_signed_out))
    }
    
    @Test
    fun mainViewModel_signOut_signOutFails_showsSignOutFailMessage() {
        val listenerSlot = slot<AccountManager.AccountListener>()
        every { accountManager.signOut(capture(listenerSlot)) } just Runs

        viewModel.signOut()

        verify { accountManager.signOut(any()) }

        listenerSlot.captured.onSignOutFailure()

        assertThat(viewModel.signOutMessage.value, equalTo(R.string.main_menu_sign_out_failed))
    }
}