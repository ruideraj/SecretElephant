package com.ruideraj.secretelephant.send;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.match.MatchExchange;

public class SendActivity extends AppCompatActivity implements SendAdapter.SendClickListener {

    private SendViewModel mViewModel;

    private RecyclerView mRecycler;
    private SendAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        mRecycler = findViewById(R.id.send_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(SendActivity.this));
        mAdapter = new SendAdapter(this);
        mRecycler.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this).get(SendViewModel.class);

        mViewModel.invitesData.observe(this, invites -> {
            if(invites != null) {
                mAdapter.setData(invites);
                findViewById(R.id.send_list_text).setVisibility(View.GONE);
                findViewById(R.id.send_list_progress).setVisibility(View.GONE);
                mRecycler.setVisibility(View.VISIBLE);
            }
        });

        mViewModel.updatedPosition.observe(this, position -> mAdapter.notifyItemChanged(position));

        if(savedInstanceState == null) {
            // If we are not recreating the Activity, get invite data and start the service.
            Intent intent = getIntent();
            MatchExchange exchange = intent.getParcelableExtra(Constants.KEY_EXCHANGE);

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
    public void onRefreshClick(int position) {
        mViewModel.onRefreshClick(position);
    }
}
