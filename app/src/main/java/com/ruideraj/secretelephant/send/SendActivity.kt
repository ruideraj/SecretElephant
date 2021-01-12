package com.ruideraj.secretelephant.send

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruideraj.secretelephant.AppLog
import com.ruideraj.secretelephant.KEY_EXCHANGE
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.ViewModelFactory
import com.ruideraj.secretelephant.main.MainActivity

class SendActivity : AppCompatActivity() {

    private val viewModel by viewModels<SendViewModel> { ViewModelFactory(this) }

    private lateinit var recycler: RecyclerView
    private lateinit var sendAdapter: SendAdapter

    private lateinit var text: TextView
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        AppLog.d("SendActivity", "onCreate")

        sendAdapter = SendAdapter(viewModel)
        recycler = findViewById<RecyclerView>(R.id.send_recycler).apply {
            layoutManager = LinearLayoutManager(this@SendActivity)
            adapter = sendAdapter
        }

        text = findViewById(R.id.send_list_text)
        progress = findViewById(R.id.send_list_progress)

        viewModel.run {
            messages.observe(this@SendActivity, { messages ->
                if (messages != null) sendAdapter.messages = messages
            })

            updatedPosition.observe(this@SendActivity, { update ->
                if (update != null) sendAdapter.notifyItemChanged(update.position)
            })

            queueFinished.observe(this@SendActivity, { goBackToMain() })

            toast.observe(this@SendActivity, { toastId ->
                if (toastId != null) Toast.makeText(this@SendActivity, toastId, Toast.LENGTH_SHORT).show()
            })

            listVisibility.observe(this@SendActivity, { visibility ->
                if (visibility != null) recycler.visibility = visibility
            })

            progressVisibility.observe(this@SendActivity, { visibility ->
                if (visibility != null) {
                    text.visibility = visibility
                    progress.visibility = visibility
                }
            })
        }

        if (savedInstanceState == null) {
            // If we are not recreating the Activity, get exchange data and start sending the messages.
            viewModel.sendMessages(intent.getParcelableExtra(KEY_EXCHANGE)!!)
        } else {
            // TODO
            // If we're recreating, we need to avoid resending messages.
            // Need to determine if there's a way (since we're currently not using a database)
            // to resume sending messages if they weren't sent. Needs to be done with the user's
            // action/permission.
        }
    }

    private fun goBackToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}