package com.ruideraj.secretelephant.match;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.ViewModelFactory;
import com.ruideraj.secretelephant.send.SendActivity;

/**
 * Activity where user's selected contacts are matched with numbers or gift recipients based on
 * which gift exchange they chose.
 */
public class MatchActivity extends AppCompatActivity {

    private static final int REQUEST_SMS = 100;

    private MatchViewModel mMatchViewModel;

    private MatchAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        mAdapter = new MatchAdapter();
        RecyclerView recycler = findViewById(R.id.match_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(MatchActivity.this);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(mAdapter);

        mMatchViewModel = ViewModelProviders.of(this, new ViewModelFactory(this))
                .get(MatchViewModel.class);

        mMatchViewModel.exchange.observe(this, exchange -> {
            if(exchange == null) return;

            if(mAdapter.getItemCount() == 0) {
                mAdapter.setData(exchange.getContacts(), exchange.getMatches(), exchange.getMode());
            }
            else {
                mAdapter.setNewMatches(exchange.getMatches());
            }
        });

        mMatchViewModel.textId.observe(this, id -> {
            if(id != null) {
                TextView text = findViewById(R.id.match_text);
                text.setText(id);
            }
        });

        mMatchViewModel.noContacts.observe(this, aVoid -> {
            Toast.makeText(this ,R.string.match_contacts_missing, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mMatchViewModel.exchange.getValue() == null) {
            Intent intent = getIntent();
            mMatchViewModel.processIntent(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_match, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reorder:
                mMatchViewModel.reorder();
                break;
            case R.id.action_send:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.SEND_SMS};
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_SMS);
                }
                else {
                    sendInvites(mMatchViewModel.exchange.getValue());
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_SMS) {
            if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Show error Toast and go back to previous screen if permission is denied.
                Toast.makeText(this, R.string.match_permission_denied_sms, Toast.LENGTH_SHORT).show();
            }
            else {
                sendInvites(mMatchViewModel.exchange.getValue());
            }
        }
    }

    private void sendInvites(MatchExchange exchange) {
        Intent intent = new Intent(this, SendActivity.class);
        intent.putExtra(Constants.KEY_EXCHANGE, exchange);
        startActivity(intent);
    }
}
