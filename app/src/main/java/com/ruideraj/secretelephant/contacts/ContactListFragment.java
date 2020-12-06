package com.ruideraj.secretelephant.contacts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruideraj.secretelephant.AppLog;
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
        AppLog.d("ContactListFragment", "onCreateView");
        mViewModel = ViewModelProviders.of(getActivity()).get(ContactsViewModel.class);

        Bundle args = getArguments();

        View root = inflater.inflate(R.layout.fragment_contact_list, container, false);

        mRecycler = root.findViewById(R.id.contacts_list_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(manager);
        mAdapter = new ContactAdapter(mViewModel, this);
        mRecycler.setAdapter(mAdapter);

        mType = args.getInt(ARG_CONTACT_TYPE);

        LiveData<List<Contact>> liveData;
        if(mType == Contact.TYPE_PHONE) {
            liveData = mViewModel.phones;
        }
        else {
            liveData = mViewModel.emails;
        }

        liveData.observe(getActivity(), list -> {
            if(list != null) {
                mAdapter.setData(list);
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
    public void onContactClick(int position) {
        mViewModel.onContactClicked(mType, position);
        mAdapter.notifyItemChanged(position);
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
