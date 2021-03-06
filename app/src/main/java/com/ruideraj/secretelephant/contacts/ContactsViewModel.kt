package com.ruideraj.secretelephant.contacts

import android.Manifest
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.ruideraj.secretelephant.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository,
                                            private val accountManager: AccountManager,
                                            private val permissionManager: PermissionManager,
                                            private val preferences: Preferences): ViewModel() {

    val phones: LiveData<List<Contact>>
        get() = phonesData
    private val phonesData = MutableLiveData<List<Contact>>()

    val emails: LiveData<List<Contact>>
        get() = emailsData
    private val emailsData = MutableLiveData<List<Contact>>()

    val selectedContacts: List<Contact>
        get() = selectedContactsList
    private val selectedContactsList: MutableList<Contact> = mutableListOf()

    private val selectedData: MutableSet<String> = mutableSetOf()

    val emailAccount: LiveData<String?>
        get() = emailAccountData
    private val emailAccountData = MutableLiveData<String?>()

    val showSelection: LiveData<Int>
        get() = showSelectionData
    private val showSelectionData = MediatorLiveData<Int>()

    val showProgress: LiveData<Int>
        get() = showProgressData
    private val showProgressData = MutableLiveData(View.GONE)

    val showSettings: LiveData<Int>
        get() = showSettingsData
    private val showSettingsData = MutableLiveData(View.GONE)

    val showContinue: LiveData<Boolean>
        get() = showContinueData
    private val showContinueData = MutableLiveData(false)

    val showContactsPermissionRationale: SharedFlow<Unit>
        get() = showContactsPermissionRationaleFlow
    private val showContactsPermissionRationaleFlow = MutableSharedFlow<Unit>()

    val requestContactsPermission: SharedFlow<Unit>
        get() = requestContactsPermissionFlow
    private val requestContactsPermissionFlow = MutableSharedFlow<Unit>()

    val selectAccount: LiveData<Void>
        get() = selectAccountData
    private val selectAccountData = SingleLiveEvent<Void>()

    private val contactUpdateFlow = MutableSharedFlow<ContactUpdate>()
    val contactUpdate: SharedFlow<ContactUpdate>
        get() = contactUpdateFlow

    val toast: LiveData<Int>
        get() = toastData
    private val toastData = SingleLiveEvent<Int>()

    val finish: LiveData<Void>
        get() = finishData
    private val finishData = SingleLiveEvent<Void>()

    private var contactsResult: ContactsResult? = null

    var filterText: String = ""
        private set

    private var filterParentJob: Job? = null

    val signInIntent: Intent
        get() = accountManager.getSignInIntent()

    val pageCount = Contact.Type.values().size

    init {
        showSelectionData.addSource(phones) { setListVisibility() }
        showSelectionData.addSource(emails) { setListVisibility() }
        showSelectionData.value = View.GONE
    }

    fun start() {
        setEmailAccount(accountManager.getAccount())

        if (permissionManager.checkPermission(Manifest.permission.READ_CONTACTS)) {
            if (phones.value == null && emails.value == null) {
                loadContacts()
            }
        } else {
            if (preferences.contactsShowPermissionDialog) {
                viewModelScope.launch {
                    showContactsPermissionRationaleFlow.emit(Unit)
                }
            } else {
                showSettingsData.value = View.VISIBLE
            }
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            val contacts = contactsRepository.loadContacts()
            contactsResult = contacts

            phonesData.value = contacts.phones
            emailsData.value = contacts.emails
        }
    }

    fun onPermissionRationaleResult(continued: Boolean) {
        preferences.contactsShowPermissionDialog = false

        if (continued) {
            viewModelScope.launch { requestContactsPermissionFlow.emit(Unit) }
        } else {
            setListVisibility()
        }
    }

    fun onRequestContactsPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            loadContacts()
        } else {
            setListVisibility()
        }
    }

    fun getPageType(page: Int) = if (page == 0) {
        Contact.Type.PHONE
    } else {
        Contact.Type.EMAIL
    }

    fun getPageTitle(page: Int) = when (getPageType(page)) {
        Contact.Type.PHONE -> "Phones"
        Contact.Type.EMAIL -> "Emails"
    }

    private fun setListVisibility() {
        if (!permissionManager.checkPermission(Manifest.permission.READ_CONTACTS)) {
            showSelectionData.value = View.GONE
            showProgressData.value = View.GONE
            showSettingsData.value = View.VISIBLE
        } else if (phones.value != null || emails.value != null) {
            showSelectionData.value = View.VISIBLE
            showProgressData.value = View.GONE
            showSettingsData.value = View.GONE
        } else {
            showSelectionData.value = View.GONE
            showProgressData.value = View.VISIBLE
            showSettingsData.value = View.GONE
        }
    }

    fun signInResult(task: Task<GoogleSignInAccount>) {
        try {
            setEmailAccount(task.getResult(ApiException::class.java))
        } catch (e: ApiException) {
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_FAILED, CommonStatusCodes.NETWORK_ERROR ->
                    toastData.value = R.string.contact_google_sign_in_error
            }
        }
    }

    private fun setEmailAccount(account: GoogleSignInAccount?) {
        if (account != null) {
            emailAccountData.value = account.email
        } else {
            emailAccountData.value = null
        }
    }

    fun filter(text: String) {
        var input = text
        val currentFilterText = filterText
        val contacts = contactsResult

        val inputDiffersFromCurrent = currentFilterText != input
        val newInputEntered = currentFilterText.isEmpty() && input.isNotEmpty()
        if (contacts != null && (inputDiffersFromCurrent || newInputEntered)) {
            filterParentJob?.let { if (!it.isCompleted) it.cancel() }

            if (input.isNotEmpty()) input = input.toLowerCase()
            filterText = input

            filterParentJob = viewModelScope.launch {
                val filteredContacts = awaitAll (
                    async(Dispatchers.Default) {
                        contacts.phones.filter { it.name.contains(input, true) }
                    },
                    async(Dispatchers.Default) {
                        contacts.emails.filter { it.name.contains(input, true) }
                    }
                )

                phonesData.value = filteredContacts[0]
                emailsData.value = filteredContacts[1]
            }
        }
    }

    fun onContactClicked(type: Contact.Type, position: Int) {
        val list = if (type == Contact.Type.PHONE) {
            phones.value
        } else {
            emails.value
        }

        if (list != null) {
            val contact = list[position]
            val data = contact.data

            val added = if (selectedData.contains(data)) {
                selectedData.remove(data)
                selectedContactsList.remove(contact)

                false
            } else {
                selectedData.add(data)
                selectedContactsList.add(contact)

                true
            }

            checkShouldShowContinue()

            if (filterText.isNotEmpty()) filter("")

            viewModelScope.launch {
                contactUpdateFlow.emit(ContactUpdate(contact.type, added, selectedContactsList.size - 1, position))
            }
        }
    }

    fun removeSelectedContact(position: Int) {
        val removedContact = selectedContactsList[position]
        selectedContactsList.removeAt(position)
        selectedData.remove(removedContact.data)

        val listPosition = if (removedContact.type == Contact.Type.PHONE) {
            phonesData.value?.let { it.indexOfFirst { contact -> contact.data == removedContact.data } }
        } else {
            emailsData.value?.let { it.indexOfFirst { contact -> contact.data == removedContact.data } }
        }

        viewModelScope.launch {
            contactUpdateFlow.emit(ContactUpdate(removedContact.type, false, position, listPosition!!))
        }

        checkShouldShowContinue()
    }

    fun onSelectAccount() = selectAccountData.call()

    private fun checkShouldShowContinue() {
        if (selectedContactsList.size == 1) {
            showContinueData.value = true
        } else if (selectedContactsList.size == 0) {
            showContinueData.value = false
        }
    }
}