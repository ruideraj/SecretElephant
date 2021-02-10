package com.ruideraj.secretelephant.contacts

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ruideraj.secretelephant.*
import com.ruideraj.secretelephant.match.MatchActivity
import kotlinx.coroutines.flow.collect

class ContactsActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_SIGN_IN = 1000

        private const val PERMISSION_DIALOG_TAG = "contactPermissionRationale"
    }

    private lateinit var recycler: RecyclerView
    private lateinit var contactsInputAdapter: ContactsInputAdapter
    private lateinit var progress: LinearLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var settingsText: TextView
    private lateinit var settingsButton: Button

    private val viewModel by viewModels<ContactsViewModel> {
        ViewModelFactory(this)
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        viewPager = findViewById(R.id.contacts_viewpager)
        val pagerAdapter = ContactsPagerAdapter(this, viewModel)
        viewPager.adapter = pagerAdapter
        tabLayout = findViewById(R.id.contacts_tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val title = viewModel.getPageTitle(position)
            AppLog.d("ContactsActivity", "Page: $position, Title: $title")
            tab.text = title
        }.attach()

        recycler = findViewById(R.id.contacts_selected_recycler)

        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.alignItems = AlignItems.CENTER
        val decoration = FlexboxItemDecoration(this)
        val space = ResourcesCompat.getDrawable(resources, R.drawable.space, null)
        decoration.setDrawable(space)
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(decoration)

        contactsInputAdapter = ContactsInputAdapter(viewModel, viewModel.selectedContacts)
        recycler.adapter = contactsInputAdapter

        progress = findViewById(R.id.contacts_progress_bar)

        settingsText = findViewById(R.id.contacts_settings_text)
        settingsButton = findViewById<Button>(R.id.contacts_settings_button).apply {
            setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        }

        viewModel.let {
            it.showSelection.observe(this, { show ->
                if (show != null) {
                    recycler.visibility = show
                    tabLayout.visibility = show
                    viewPager.visibility = show
                }
            })

            it.showProgress.observe(this, { show ->
                if (show != null) progress.visibility = show
            })

            it.showSettings.observe(this, { show ->
                if (show != null) {
                    settingsText.visibility = show
                    settingsButton.visibility = show
                }
            })

            lifecycleScope.launchWhenStarted {
                it.contactUpdate.collect { update ->
                    if (update.added) {
                        // Update last two items, the newly added item and the EditText
                        contactsInputAdapter.notifyItemInserted(update.selectedPosition)
                        contactsInputAdapter.notifyItemChanged(contactsInputAdapter.itemCount - 1)
                    } else {
                        // TODO Not using notifyItemRemoved() due to visual issues from the update animation
                        contactsInputAdapter.notifyDataSetChanged()
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                it.showContactsPermissionRationale.collect {
                    val should = ActivityCompat.shouldShowRequestPermissionRationale(
                            this@ContactsActivity, Manifest.permission.READ_CONTACTS)
                    AppLog.d("ContactsActivity", "should: $should")
                    PermissionRationaleDialog().show(supportFragmentManager, PERMISSION_DIALOG_TAG)
                }
            }

            permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                viewModel.onRequestContactsPermissionResult(isGranted)
            }

            lifecycleScope.launchWhenStarted {
                it.requestContactsPermission.collect { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }
            }

            it.showContinue.observe(this, { invalidateOptionsMenu() })

            it.selectAccount.observe(this, { requestEmailAccount() })

            it.toast.observe(this, { stringId ->
                if (stringId != null) Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
            })

            it.finish.observe(this, { finish() })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                viewModel.signInResult(task)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val show = viewModel.showContinue.value
        if (show != null && show) {
            menuInflater.inflate(R.menu.menu_contacts, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_continue -> {
                val mode = intent.getSerializableExtra(KEY_MODE) as Mode
                val contactList = ArrayList(viewModel.selectedContacts)

                val matchIntent = Intent(this, MatchActivity::class.java).apply {
                    putExtra(KEY_MODE, mode)
                    putExtra(KEY_SELECTED, contactList)
                }
                startActivity(matchIntent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun requestEmailAccount() {
        val signInIntent = viewModel.signInIntent
        startActivityForResult(signInIntent, REQUEST_SIGN_IN)
    }

}