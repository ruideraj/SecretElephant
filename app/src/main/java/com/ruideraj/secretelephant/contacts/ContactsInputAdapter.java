package com.ruideraj.secretelephant.contacts;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ruideraj.secretelephant.R;

import java.util.List;

/**
 * Adapter class for the Contact Input field in ContactsActivity.
 */
public class ContactsInputAdapter extends RecyclerView.Adapter {

    private static final int TYPE_CONTACT = 0;
    private static final int TYPE_EDIT = 1;

    private ContactsViewModel mViewModel;
    private List<Contact> mContacts;
    private TextWatcher mTextWatcher;

    public ContactsInputAdapter(ContactsViewModel viewModel, List<Contact> contacts, TextWatcher textWatcher) {
        mViewModel = viewModel;
        mContacts = contacts;
        mTextWatcher = textWatcher;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int layoutId = viewType == TYPE_CONTACT ?
                R.layout.contact_input_list_item : R.layout.contact_input_list_edit;
        View view = inflater.inflate(layoutId, parent, false);

        if(viewType == TYPE_EDIT) {
            ((EditText) view.findViewById(R.id.contact_edit)).addTextChangedListener(mTextWatcher);
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder vh = (ViewHolder) viewHolder;

        int type = getItemViewType(i);
        if(type == TYPE_CONTACT) {
            Contact contact = mContacts.get(i);
            vh.contactName.setText(contact.getName());
        }
        else if(type == TYPE_EDIT) {
            vh.edit.setText(mViewModel.getSearchText());
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size() + 1;  // +1 to add the EditText as the last item
    }

    @Override
    public int getItemViewType(int position) {
        return position == mContacts.size() ? TYPE_EDIT : TYPE_CONTACT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView contactName;
        private EditText edit;

        public ViewHolder(View view, int type) {
            super(view);
            if(type == TYPE_CONTACT) {
                contactName = (TextView) view;
            }
            else {
                edit = view.findViewById(R.id.contact_edit);
            }
        }

    }
}
