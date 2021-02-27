package com.ruideraj.secretelephant.match

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.PermissionManager
import com.ruideraj.secretelephant.Preferences
import com.ruideraj.secretelephant.contacts.Contact
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchViewModelTest {

    @Rule @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var matchmaker: Matchmaker

    @MockK
    private lateinit var accountManager: AccountManager

    @MockK
    private lateinit var permissionsManager: PermissionManager

    @MockK
    private lateinit var preferences: Preferences

    @MockK
    private lateinit var intent: Intent

    private var viewModel: MatchViewModel? = null

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        viewModel = MatchViewModel(matchmaker, permissionsManager, preferences)
    }

    @Test
    fun processIntentWithContacts() {
        val list = arrayListOf(Contact("Person1", Contact.Type.PHONE, "123-456-7890"),
                Contact("Person2", Contact.Type.PHONE, "456-123-0987"),
                Contact("Person3", Contact.Type.PHONE, "987-123-4560"))
        every { intent.getParcelableArrayListExtra<Contact>(any()) } returns list

        viewModel!!.setupExchange(intent)
    }

}