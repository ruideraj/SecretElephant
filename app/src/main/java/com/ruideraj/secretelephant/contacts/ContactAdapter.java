package com.ruideraj.secretelephant.contacts;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ruideraj.secretelephant.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for populating the list in ContactsListFragment
 */
public class ContactAdapter extends RecyclerView.Adapter implements Filterable {

    private List<Contact> mContacts;
    private List<Contact> mFilteredContacts;
    private ContactFilter mFilter;
    private ContactClickListener mClickListener;

    public ContactAdapter(@NonNull List<Contact> contacts, ContactClickListener clickListener) {
        mContacts = contacts;
        mFilteredContacts = contacts;  // Initialize with full list.
        mClickListener = clickListener;
        mFilter = new ContactFilter();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contact_list_item, parent, false);

        return new ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;

        // Fill in contact info
        Contact contact = mFilteredContacts.get(position);

        vh.name.setText(contact.getName());
        vh.data.setText(contact.getData());

        // Show selected status with background color.
        if(contact.isSelected()) {
            vh.itemView.setBackgroundColor(ContextCompat.getColor(vh.itemView.getContext(),
                    R.color.bg_contact_selected));
        }
        else {
            vh.itemView.setBackgroundColor(ContextCompat.getColor(vh.itemView.getContext(),
                    R.color.bg_activity));
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public interface ContactClickListener {
        void onContactClick(int position, Contact contact);
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ContactClickListener mClickListener;
        TextView name;
        TextView data;

        public ViewHolder(View view, ContactClickListener clickListener) {
            super(view);
            view.setOnClickListener(this);
            mClickListener = clickListener;
            name = view.findViewById(R.id.contactName);
            data = view.findViewById(R.id.contactData);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Contact contact = mFilteredContacts.get(position);
                mClickListener.onContactClick(position, contact);
            }

        }
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Contact> filteredContacts;

            if(constraint == null || constraint.toString().isEmpty()) {
                filteredContacts = mContacts;
            }
            else {
                filteredContacts = new ArrayList<>();
                for(Contact contact : mContacts) {
                    if(contact.getName().toLowerCase().contains(constraint)) {
                        filteredContacts.add(contact);
                    }
                }
            }

            results.values = filteredContacts;
            results.count = filteredContacts.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredContacts = (List<Contact>) results.values;
            notifyDataSetChanged();
        }
    }
}