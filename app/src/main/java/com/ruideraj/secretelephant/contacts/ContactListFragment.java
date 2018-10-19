package com.ruideraj.secretelephant.contacts;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruideraj.secretelephant.ADB;
import com.ruideraj.secretelephant.R;

import java.util.List;

public class ContactListFragment extends Fragment implements
        ContactAdapter.ContactClickListener, View.OnClickListener {

    private static final String ARG_CONTACT_TYPE = "contactType";

    private ContactsViewModel mViewModel;
    private int mType;
    private RecyclerView mRecycler;
    private ContactAdapter mAdapter;

    public static ContactListFragment newInstance(int contactType) {
        Bundle args = new Bundle();
        args.putInt(ARG_CONTACT_TYPE, contactType);

        ContactListFragment fragment = new ContactListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ADB.d("ContactListFragment", "onCreateView");
        mViewModel = ViewModelProviders.of(getActivity()).get(ContactsViewModel.class);

        Bundle args = getArguments();

        View root = inflater.inflate(R.layout.fragment_contact_list, container, false);

        mRecycler = root.findViewById(R.id.contacts_list_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(manager);

        mType = args.getInt(ARG_CONTACT_TYPE);

        mViewModel.contacts.observe(getActivity(), contactsResult -> {
            if(contactsResult != null) {
                List<Contact> contacts;
                if(mType == Contact.TYPE_PHONE) {
                    contacts = contactsResult.phones;
                }
                else {
                    contacts = contactsResult.emails;
                }

                mAdapter = new ContactAdapter(contacts, ContactListFragment.this);
                mRecycler.setAdapter(mAdapter);
            }
        });

        if(mType == Contact.TYPE_EMAIL) {
            if(TextUtils.isEmpty(mViewModel.emailAccount.getValue())) {
                root.findViewById(R.id.contacts_list_email_overlay).setVisibility(View.VISIBLE);
                root.findViewById(R.id.contacts_list_email_select).setOnClickListener(this);
            }
            mViewModel.emailAccount.observe(getActivity(),
                    email -> setOverlayVisible(TextUtils.isEmpty(email)));
        }

        mViewModel.searchText.observe(getActivity(), this::filter);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.contacts_list_email_select:
                mViewModel.onSelectAccount();
                break;
        }
    }

    @Override
    public void onContactClick(int position, Contact contact) {
        mViewModel.onContactClicked(contact);
        mAdapter.notifyItemChanged(position);
    }

    public void filter(CharSequence constraint) {
        mAdapter.getFilter().filter(constraint);
    }

    public void setOverlayVisible(boolean visible) {
        View root = getView();
        if(root != null) {
            View overlay = root.findViewById(R.id.contacts_list_email_overlay);
            if(visible) {
                mRecycler.setVisibility(View.GONE);
                overlay.setVisibility(View.VISIBLE);
            }
            else {
                mRecycler.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.GONE);
            }
        }
    }
}
