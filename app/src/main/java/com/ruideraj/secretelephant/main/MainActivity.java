package com.ruideraj.secretelephant.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ruideraj.secretelephant.ADB;
import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.ViewModelFactory;
import com.ruideraj.secretelephant.contacts.ContactsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findViewById(R.id.main_button_elephant).setOnClickListener(this);
        findViewById(R.id.main_button_santa).setOnClickListener(this);

        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication())).get(MainViewModel.class);

        mViewModel.signedIn.observe(this, signedIn -> invalidateOptionsMenu());

        mViewModel.signOutMessage.observe(this, messageId -> {
            if(messageId == null) return;

            Toast.makeText(getApplicationContext(), messageId,
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem signOutItem = menu.getItem(0);

        signOutItem.setVisible(mViewModel.signedIn.getValue());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                mViewModel.signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ContactsActivity.class);
        switch(v.getId()) {
            case R.id.main_button_elephant:
                intent.putExtra(Constants.KEY_MODE, Constants.MODE_ELEPHANT);
                break;
            case R.id.main_button_santa:
                intent.putExtra(Constants.KEY_MODE, Constants.MODE_SANTA);
                break;
        }

        startActivity(intent);
    }
}
