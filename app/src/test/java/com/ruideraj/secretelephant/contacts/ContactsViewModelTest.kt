package com.ruideraj.secretelephant.contacts

import android.util.Log
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.PermissionManager
import com.ruideraj.secretelephant.Preferences
import com.ruideraj.secretelephant.R
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private var contactsRepository = mockk<ContactsRepository>()

    @RelaxedMockK
    private lateinit var accountManager: AccountManager

    @RelaxedMockK
    private lateinit var permissionManager: PermissionManager

    @RelaxedMockK
    private lateinit var preferences: Preferences

    @MockK
    private lateinit var signInTask: Task<GoogleSignInAccount>

    @MockK
    private lateinit var account: GoogleSignInAccount

    private lateinit var viewModel: ContactsViewModel

    @Before
    @ExperimentalCoroutinesApi
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testCoroutineDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        viewModel = ContactsViewModel(contactsRepository, accountManager, permissionManager, preferences)
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
        every { permissionManager.checkPermission(any()) } returns true

        viewModel.start()

        coVerify { contactsRepository.loadContacts() }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun contactsViewModel_start_permissionNotGranted_permissionRequested() = runBlockingTest {
        every { permissionManager.checkPermission(any()) } returns false

        var permissionsRequest = false
        val collectJob = launch {
            viewModel.requestContactsPermission.collect {
                permissionsRequest = true
            }
        }

        viewModel.start()

        assertThat(permissionsRequest, equalTo(true))

        collectJob.cancel()
    }

    @Test
    fun contactsViewModel_signInResult_success_emailSet() {
        every { signInTask.getResult(any<Class<Throwable>>()) } returns account

        val email = "fake@email.com"
        every { account.email } returns email

        viewModel.signInResult(signInTask)
        assertThat(viewModel.emailAccount.value, equalTo(email))
    }

    @Test
    fun contactsViewModel_signInResult_error_toastShown() {
        val errorStatus = Status(GoogleSignInStatusCodes.SIGN_IN_FAILED)
        every { signInTask.getResult(any<Class<Throwable>>()) } throws ApiException(errorStatus)

        viewModel.signInResult(signInTask)
        assertThat(viewModel.emailAccount.value, equalTo(null))
        assertThat(viewModel.toast.value, equalTo(R.string.contact_google_sign_in_error))
    }
}