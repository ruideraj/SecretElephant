package com.ruideraj.secretelephant.match;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruideraj.secretelephant.Constants;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.contacts.Contact;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter {

    private List<Contact> mContacts;
    private int[] mMatches;
    private boolean[] mMatchesShown;  // Array containing which participants have their match shown.
    private int mMode;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.match_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        Contact contact = mContacts.get(position);

        vh.participant.setText(contact.getName());

        boolean showMatch = mMatchesShown[position];

        if(showMatch) {
            if(mMode == Constants.MODE_SANTA) {
                Contact recipient = mContacts.get(mMatches[position]);
                vh.recipient.setText(recipient.getName());
            }
            else if(mMode == Constants.MODE_ELEPHANT) {
                int orderPosition = mMatches[position] + 1; // Add 1 to account for 0-based index.
                vh.recipient.setText("" + orderPosition);
            }

            vh.recipient.setTypeface(null, Typeface.NORMAL);
        }
        else {
            vh.recipient.setText("?");
            vh.recipient.setTypeface(null, Typeface.BOLD);
        }

    }

    @Override
    public int getItemCount() {
        return mContacts == null ? 0 : mContacts.size();
    }

    public void setData(List<Contact> contacts, int[] matches, int mode) {
        mContacts = contacts;
        mMatches = matches;
        mMatchesShown = new boolean[contacts.size()];
        mMode = mode;
        notifyDataSetChanged();
    }

    public void setNewMatches(int[] newMatches) {
        mMatches = newMatches;
        notifyItemRangeChanged(0, mContacts.size());
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView participant;
        TextView recipient;

        public ViewHolder(View view) {
            super(view);
            participant = view.findViewById(R.id.match_name);
            recipient = view.findViewById(R.id.match_recipient);
            recipient.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mMatchesShown[position] = !mMatchesShown[position];
            notifyItemChanged(position);
        }
    }
}
