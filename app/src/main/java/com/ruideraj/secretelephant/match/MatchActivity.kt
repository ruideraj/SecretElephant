package com.ruideraj.secretelephant.match

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruideraj.secretelephant.AppLog
import com.ruideraj.secretelephant.KEY_EXCHANGE
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.ViewModelFactory
import com.ruideraj.secretelephant.send.SendActivity
import kotlinx.coroutines.flow.collect

class MatchActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_DIALOG_TAG = "matchPermissionRationale"
        private const val SETTINGS_DIALOG_TAG = "matchSettingsDialog"
    }

    private val viewModel by viewModels<MatchViewModel> { ViewModelFactory(this) }

    private lateinit var permissionsLauncher: ActivityResultLauncher<String>

    private lateinit var adapter: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        val recycler = findViewById<RecyclerView>(R.id.match_recycler)
        adapter = MatchAdapter()
        val layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter

        viewModel.let {
            it.exchange.observe(this, { exchange ->
                if (exchange != null) {
                    if (adapter.itemCount == 0) {
                        adapter.setData(exchange)
                    } else {
                        adapter.setNewMatches(exchange)
                    }
                }
            })

            it.textId.observe(this, { id ->
                if (id != null) { findViewById<TextView>(R.id.match_text).setText(id) }
            })

            it.noContacts.observe(this, {
                Toast.makeText(this, R.string.match_contacts_missing, Toast.LENGTH_SHORT).show()
                finish()
            })

            it.sendMessages.observe(this, { exchange ->
                val intent = Intent(this, SendActivity::class.java).apply {
                    putExtra(KEY_EXCHANGE, exchange)
                }
                startActivity(intent)
            })

            lifecycleScope.launchWhenStarted {
                it.showSmsPermissionRationale.collect {
                    val dialog = supportFragmentManager.findFragmentByTag(PERMISSION_DIALOG_TAG)
                    if (dialog != null) (dialog as DialogFragment).dismiss()

                    MatchPermissionRationaleDialog().show(supportFragmentManager, PERMISSION_DIALOG_TAG)
                }
            }

            permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                it.onSmsPermissionResult(isGranted)
            }

            lifecycleScope.launchWhenStarted {
                it.requestSmsPermission.collect { permissionsLauncher.launch(Manifest.permission.SEND_SMS) }
            }

            lifecycleScope.launchWhenStarted {
                it.showAppSettingsDialog.collect {
                    val dialog = supportFragmentManager.findFragmentByTag(SETTINGS_DIALOG_TAG)
                    if (dialog != null) (dialog as DialogFragment).dismiss()

                    MatchAppSettingsDialog().show(supportFragmentManager, SETTINGS_DIALOG_TAG)
                }
            }

            it.toast.observe(this, { textId ->
                Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
            })
        }
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
}