package com.ruideraj.secretelephant.contacts;

import android.Manifest;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxItemDecoration;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.ruideraj.secretelephant.ConstantsKt;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.ViewModelFactory;
import com.ruideraj.secretelephant.match.MatchActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements TextWatcher {

    private static final int REQUEST_CONTACTS = 100;
    private static final int REQUEST_SIGN_IN = 1000;

    private RecyclerView mRecycler;
    private ContactsInputAdapter mAdapter;
    private LinearLayout mProgress;
    private ViewPager mViewPager;

    private ContactsViewModel mContactsViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mContactsViewModel = ViewModelProviders.of(this,
                new ViewModelFactory(this)).get(ContactsViewModel.class);

        mViewPager = findViewById(R.id.contacts_viewpager);
        ContactsPagerAdapter adapter = new ContactsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        mRecycler = findViewById(R.id.contacts_selected_recycler);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.CENTER);
        FlexboxItemDecoration decoration = new FlexboxItemDecoration(this);
        Drawable space = ResourcesCompat.getDrawable(this.getResources(), R.drawable.space, null);
        decoration.setDrawable(space);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addItemDecoration(decoration);
        mAdapter = new ContactsInputAdapter(mContactsViewModel, mContactsViewModel.selectedContacts,
                this);
        mRecycler.setAdapter(mAdapter);

        mProgress = findViewById(R.id.contacts_progress_bar);

        mContactsViewModel.showSelection.observe(this, show -> {
            if(show != null) {
                mRecycler.setVisibility(show);
                mViewPager.setVisibility(show);
            }
        });

        mContactsViewModel.showProgress.observe(this, show -> {
            if(show != null) {
                mProgress.setVisibility(show);
            }
        });

        mContactsViewModel.updatedContact.observe(this, added -> {
            if(added == null) return;

            if(added) {
                // Update last two items, the newly added item and the EditText
                mAdapter.notifyItemRangeChanged(mAdapter.getItemCount() - 2, 2);
            }
            else {
                mAdapter.notifyDataSetChanged();
            }
        });

        mContactsViewModel.requestPermission.observe(this, aVoid -> {
            String[] permissions = {Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CONTACTS);
        });

        mContactsViewModel.showContinue.observe(this, showContinue -> invalidateOptionsMenu());

        mContactsViewModel.selectAccount.observe(this, aVoid -> requestEmailAccount());

        mContactsViewModel.toast.observe(this, stringId -> {
            if(stringId != null) Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
        });

        mContactsViewModel.finish.observe(this, aVoid -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mContactsViewModel.start(ContextCompat
                .checkSelfPermission(this, Manifest.permission.READ_CONTACTS));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                mContactsViewModel.signInResult(task);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean show = mContactsViewModel.showContinue.getValue();
        if(show != null && show) {
            MenuInflater inflater = this.getMenuInflater();
            inflater.inflate(R.menu.menu_contacts, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_continue:
                Intent intent = new Intent(this, MatchActivity.class);
                int mode = getIntent().getIntExtra(ConstantsKt.KEY_MODE, ConstantsKt.MODE_ELEPHANT);
                List<Contact> selectedContacts = mContactsViewModel.selectedContacts;
                ArrayList<Contact> contactList = new ArrayList<>(selectedContacts);

                intent.putExtra(ConstantsKt.KEY_MODE, mode);
                intent.putExtra(ConstantsKt.KEY_SELECTED, contactList);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CONTACTS:
                mContactsViewModel.onRequestPermissionsResult(grantResults);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mContactsViewModel.search(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    private void requestEmailAccount() {
        // TODO
        // Should check for Google Api Availability and display appropriate message if
        // not available (missing, needs updating, etc.)
        Intent signInIntent = mContactsViewModel.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_SIGN_IN);
    }
}
