package com.ruideraj.secretelephant.match

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.Runner
import com.ruideraj.secretelephant.contacts.Contact
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MatchViewModelTest {

    @Rule @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var runner: Runner

    @MockK
    private lateinit var accountManager: AccountManager

    @MockK
    private lateinit var intent: Intent

    private var viewModel: MatchViewModel? = null

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        viewModel = MatchViewModel(runner, accountManager)
    }

    @Test
    fun processIntentWithContacts() {
        val list = arrayListOf(Contact("Person1", Contact.Type.PHONE, "123-456-7890"),
                Contact("Person2", Contact.Type.PHONE, "456-123-0987"),
                Contact("Person3", Contact.Type.PHONE, "987-123-4560"))
        every { intent.getParcelableArrayListExtra<Contact>(any()) } returns list

        viewModel!!.processIntent(intent)
    }

}