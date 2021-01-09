package com.ruideraj.secretelephant.match

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruideraj.secretelephant.KEY_EXCHANGE
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.ViewModelFactory
import com.ruideraj.secretelephant.send.SendActivity

class MatchActivity : AppCompatActivity() {

    private val viewModel by viewModels<MatchViewModel> { ViewModelFactory(this) }

    private lateinit var adapter: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        val recycler = findViewById<RecyclerView>(R.id.match_recycler)
        adapter = MatchAdapter()
        val layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter

        viewModel.exchange.observe(this, { exchange ->
            if (exchange != null) {
                if (adapter.itemCount == 0) {
                    adapter.setData(exchange)
                } else {
                    adapter.setNewMatches(exchange)
                }
            }
        })

        viewModel.textId.observe(this, { id ->
            if (id != null) { findViewById<TextView>(R.id.match_text).setText(id) }
        })

        viewModel.noContacts.observe(this, {
            Toast.makeText(this, R.string.match_contacts_missing, Toast.LENGTH_SHORT).show()
            finish()
        })

        viewModel.sendMessages.observe(this, { exchange ->
            val intent = Intent(this, SendActivity::class.java).apply {
                putExtra(KEY_EXCHANGE, exchange)
            }
            startActivity(intent)
        })

        viewModel.toast.observe(this, { textId ->
            Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.exchange.value == null) {
            viewModel.setupExchange(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_match, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reorder -> {
                viewModel.reorder()
                return true
            }
            R.id.action_send -> {
                viewModel.send()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        viewModel.onPermissionsResult(requestCode, permissions, grantResults)
    }
}