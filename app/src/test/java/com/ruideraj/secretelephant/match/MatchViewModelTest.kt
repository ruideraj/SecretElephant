package com.ruideraj.secretelephant.match

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.Runner
import com.ruideraj.secretelephant.contacts.Contact
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MatchViewModelTest {

    @Rule @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var runner: Runner

    @Mock
    private lateinit var accountManager: AccountManager

    @Mock
    private lateinit var intent: Intent

    private var viewModel: MatchViewModel? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = MatchViewModel(runner, accountManager)
    }

    @Test
    fun processIntentWithContacts() {
        val list = arrayListOf(Contact("Person1", Contact.Type.PHONE, "123-456-7890"),
                Contact("Person2", Contact.Type.PHONE, "456-123-0987"),
                Contact("Person3", Contact.Type.PHONE, "987-123-4560"))
        `when`(intent!!.getParcelableArrayListExtra<Contact>(any())).thenReturn(list)

        viewModel!!.processIntent(intent)
    }

}