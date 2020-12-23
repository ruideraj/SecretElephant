package com.ruideraj.secretelephant.contacts

import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.R
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ContactsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private var contactsRepository = mockk<ContactsRepository>()

    @Mock
    private lateinit var accountManager: AccountManager

    @Mock
    private lateinit var signInTask: Task<GoogleSignInAccount>

    @Mock
    private lateinit var account: GoogleSignInAccount

    private lateinit var viewModel: ContactsViewModel

    @Before
    @ExperimentalCoroutinesApi
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
        MockitoAnnotations.initMocks(this)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        viewModel = ContactsViewModel(contactsRepository, accountManager)
    }

    @After
    @ExperimentalCoroutinesApi
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun contactsViewModel_initialState_progressVisible() {
        assertThat(viewModel.showSelection.value, equalTo(View.GONE))
        assertThat(viewModel.showProgress.value, equalTo(View.VISIBLE))
    }

    @Test
    fun contactsViewModel_start_permissionGranted_contactsLoaded() {
        val mockResult = mockk<ContactsResult>()
        every { mockResult.phones } returns mockk()
        every { mockResult.emails } returns mockk()
        coEvery { contactsRepository.loadContacts() } returns mockResult

        val permission = PackageManager.PERMISSION_GRANTED
        viewModel.start(permission)

        coVerify { contactsRepository.loadContacts() }
    }

    @Test
    fun contactsViewModel_start_permissionNotGranted_permissionRequested() {
        val mockObserver: Observer<Void> = mock()
        viewModel.requestPermission.observeForever(mockObserver)

        val permission = PackageManager.PERMISSION_DENIED
        viewModel.start(permission)

        verify(mockObserver).onChanged(null)

        viewModel.requestPermission.removeObserver(mockObserver)
    }

    @Test
    fun contactsViewModel_signInResult_success_emailSet() {
        whenever(signInTask.getResult(any<Class<Throwable>>())).thenReturn(account)
        val email = "fake@email.com"
        whenever(account.email).thenReturn(email)

        viewModel.signInResult(signInTask)
        assertThat(viewModel.emailAccount.value, equalTo(email))
    }

    @Test
    fun contactsViewModel_signInResult_error_toastShown() {
        val errorStatus = Status(GoogleSignInStatusCodes.SIGN_IN_FAILED)
        whenever(signInTask.getResult(any<Class<Throwable>>())).thenThrow(ApiException(errorStatus))

        viewModel.signInResult(signInTask)
        assertThat(viewModel.emailAccount.value, equalTo(null))
        assertThat(viewModel.toast.value, equalTo(R.string.contact_google_sign_in_error))
    }
}