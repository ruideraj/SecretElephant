package com.ruideraj.secretelephant.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ruideraj.secretelephant.KEY_MODE
import com.ruideraj.secretelephant.Mode
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.ViewModelFactory
import com.ruideraj.secretelephant.contacts.ContactsActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        findViewById<Button>(R.id.main_button_elephant).setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java).apply {
                putExtra(KEY_MODE, Mode.ELEPHANT)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.main_button_santa).setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java).apply {
                putExtra(KEY_MODE, Mode.SANTA)
            }
            startActivity(intent)
        }

        viewModel.signedIn.observe(this, { invalidateOptionsMenu() })

        viewModel.signOutMessage.observe(this, { messageId ->
            messageId?.let {
                Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.getItem(0).isVisible = viewModel.signedIn.value ?: false

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                viewModel.signOut()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}