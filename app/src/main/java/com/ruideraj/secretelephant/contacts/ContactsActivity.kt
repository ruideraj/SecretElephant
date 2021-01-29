    package com.ruideraj.secretelephant.contacts

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

    class ContactsActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CONTACTS = 100
        private const val REQUEST_SIGN_IN = 1000
    }

    private lateinit var recycler: RecyclerView
    private lateinit var contactsInputAdapter: ContactsInputAdapter
    private lateinit var progress: LinearLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val viewModel by viewModels<ContactsViewModel> {
        ViewModelFactory(this)
    }

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

            it.contactUpdate.observe(this, { update ->
                if (update.added) {
                    // Update last two items, the newly added item and the EditText
                    contactsInputAdapter.notifyItemInserted(update.selectedPosition)
                    contactsInputAdapter.notifyItemChanged(contactsInputAdapter.itemCount - 1)
                } else {
                    // TODO Not using notifyItemRemoved() due to visual issues from the update animation
                    contactsInputAdapter.notifyDataSetChanged()
                }
            })

            it.requestPermission.observe(this, {
                val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CONTACTS)
            })

            it.showContinue.observe(this, { invalidateOptionsMenu() })

            it.selectAccount.observe(this, { requestEmailAccount() })

            it.toast.observe(this, { stringId ->
                if (stringId != null) Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
            })

            it.finish.observe(this, { finish() })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS))
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CONTACTS -> viewModel.onRequestPermissionsResult(grantResults)
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