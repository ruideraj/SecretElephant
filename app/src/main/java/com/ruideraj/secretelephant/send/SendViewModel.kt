package com.ruideraj.secretelephant.send

import android.view.View
import androidx.lifecycle.*
import com.ruideraj.secretelephant.AccountManager
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.SingleLiveEvent
import com.ruideraj.secretelephant.match.MatchExchange
import kotlinx.coroutines.launch
import javax.inject.Inject

class SendViewModel @Inject constructor(private val repository: SendRepository,
                                        private val accountManager: AccountManager) : ViewModel() {

    val invitesData: LiveData<List<Message>> = repository.messages
    val updatedPosition: LiveData<SendRepository.Update> = repository.lastUpdatedPosition

    val queueFinished: LiveData<Unit>
        get() = queueFinishedData
    private val queueFinishedData = SingleLiveEvent<Unit>()

    val toast: LiveData<Int>
        get() = toastData
    private val toastData = SingleLiveEvent<Int>()

    val listVisibility: LiveData<Int>
        get() = listVisibilityData
    private val listVisibilityData = MutableLiveData(View.GONE)

    val progressVisibility: LiveData<Int>
        get() = progressVisibilityData
    private val progressVisibilityData = MutableLiveData(View.VISIBLE)

    private val updateObserver: Observer<SendRepository.Update> = Observer { update -> onUpdatedPosition(update) }

    private var totalMessages = 0
    private var messagesSent = 0

    init {
        repository.lastUpdatedPosition.observeForever(updateObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repository.lastUpdatedPosition.removeObserver(updateObserver)
    }

    fun sendInvites(exchange: MatchExchange) {
        repository.setEmailAccount(accountManager.getAccount()?.email)

        totalMessages = exchange.contacts.size
        messagesSent = 0

        viewModelScope.launch {
            repository.send(exchange)
        }

        progressVisibilityData.value = View.GONE
        listVisibilityData.value = View.VISIBLE
    }

    fun resendMessage(position: Int) {
        viewModelScope.launch {
            repository.resend(position)
        }
    }

    private fun onUpdatedPosition(update: SendRepository.Update) {
        if (update.status == Message.Status.SENT) {
            messagesSent++
        }

        if (messagesSent == totalMessages) {
            toastData.value = R.string.send_successful
            queueFinishedData.call()
        }
    }

}
