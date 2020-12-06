package com.ruideraj.secretelephant.send;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ruideraj.secretelephant.ConstantsKt;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.ViewModelFactory;
import com.ruideraj.secretelephant.main.MainActivity;
import com.ruideraj.secretelephant.match.MatchExchange;

public class SendActivity extends AppCompatActivity implements SendAdapter.SendClickListener {

    private SendViewModel mViewModel;

    private RecyclerView mRecycler;
    private SendAdapter mAdapter;

    private TextView mText;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        mRecycler = findViewById(R.id.send_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(SendActivity.this));
        mAdapter = new SendAdapter(this);
        mRecycler.setAdapter(mAdapter);

        mText = findViewById(R.id.send_list_text);
        mProgress = findViewById(R.id.send_list_progress);

        mViewModel = ViewModelProviders.of(this,
                new ViewModelFactory(this)).get(SendViewModel.class);

        mViewModel.invitesData.observe(this, invites -> {
            if(invites != null) {
                mAdapter.setData(invites);
            }
        });

        mViewModel.updatedPosition.observe(this, position -> {
            if(position != null) {
                mAdapter.notifyItemChanged(position);
            }
        });

        mViewModel.queueFinished.observe(this, aVoid -> goBackToMain());

        mViewModel.toast.observe(this, stringId -> {
            if(stringId != null) Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
        });

        mViewModel.listVisibility.observe(this, visibility -> {
            if(visibility != null) {
                mRecycler.setVisibility(visibility);
            }
        });

        mViewModel.progressVisibility.observe(this, visibility -> {
            if(visibility != null) {
                mText.setVisibility(visibility);
                mProgress.setVisibility(visibility);
            }
        });

        if(savedInstanceState == null) {
            // If we are not recreating the Activity, get invite data and start the service.
            Intent intent = getIntent();
            MatchExchange exchange = intent.getParcelableExtra(ConstantsKt.KEY_EXCHANGE);

            mViewModel.sendInvites(exchange);
        }
        else {
            // TODO
            // If we're recreating, we need to avoid resending messages.
            // Need to determine if there's a way (since we're currently not using a database)
            // to resume sending messages if they weren't sent. Needs to be done with the user's
            // action/permission.
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // TODO Cancel sending invites when user backs out.
    }

    @Override
    public void onRefreshClick(int position) {
        mViewModel.onRefreshClick(position);
    }

    private void goBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
