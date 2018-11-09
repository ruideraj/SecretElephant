package com.ruideraj.secretelephant.send;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruideraj.secretelephant.ADB;
import com.ruideraj.secretelephant.R;
import com.ruideraj.secretelephant.contacts.Contact;

import java.util.List;

public class SendAdapter extends RecyclerView.Adapter {

    private List<SendInvite> mInvites;
    private SendClickListener mListener;

    public SendAdapter(SendClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.send_list_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        SendInvite invite = mInvites.get(position);

        int type = invite.getContact().getType();
        if(type == Contact.TYPE_PHONE) {
            vh.icon.setImageResource(R.drawable.ic_chat_black_24dp);
        }
        else {
            vh.icon.setImageResource(R.drawable.ic_email_black_24dp);
        }

        vh.name.setText(invite.getContact().getName());

        int status = invite.getStatus();
        switch(status) {
            case SendInvite.SENT:
                vh.progress.setVisibility(View.GONE);
                vh.warning.setVisibility(View.GONE);
                vh.refresh.setVisibility(View.GONE);
                vh.check.setVisibility(View.VISIBLE);
                vh.buttonLayout.setVisibility(View.VISIBLE);
                break;
            case SendInvite.ERROR:
                vh.progress.setVisibility(View.GONE);
                vh.check.setVisibility(View.GONE);
                vh.warning.setVisibility(View.VISIBLE);
                vh.refresh.setVisibility(View.VISIBLE);
                vh.buttonLayout.setVisibility(View.VISIBLE);
                break;
            case SendInvite.IN_PROGRESS:
                vh.buttonLayout.setVisibility(View.GONE);
                vh.progress.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mInvites != null ? mInvites.size() : 0;
    }

    public void setData(List<SendInvite> invites) {
        mInvites = invites;
        notifyDataSetChanged();
    }

    public interface SendClickListener {
        void onRefreshClick(int position);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SendClickListener mListener;

        ImageView icon;
        TextView name;
        ProgressBar progress;
        LinearLayout buttonLayout;
        ImageView refresh;
        ImageView warning;
        ImageView check;

        public ViewHolder(View view, SendClickListener listener) {
            super(view);
            mListener = listener;
            icon = view.findViewById(R.id.send_icon);
            name = view.findViewById(R.id.send_name);
            progress = view.findViewById(R.id.send_progress);
            buttonLayout = view.findViewById(R.id.send_button_layout);
            refresh = view.findViewById(R.id.send_refresh);
            refresh.setOnClickListener(this);
            warning = view.findViewById(R.id.send_warning);
            check = view.findViewById(R.id.send_check);
        }

        @Override
        public void onClick(View v) {
            mListener.onRefreshClick(getAdapterPosition());
        }
    }

}
