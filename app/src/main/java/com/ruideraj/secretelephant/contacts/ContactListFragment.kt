package com.ruideraj.secretelephant.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.ViewModelFactory

class ContactListFragment : Fragment() {

    companion object {
        const val ARG_CONTACT_TYPE = "contactType"

        @JvmStatic
        fun newInstance(contactType: Contact.Type): ContactListFragment {
            val args = Bundle()
            args.putSerializable(ARG_CONTACT_TYPE, contactType)

            return ContactListFragment().apply {
                arguments = args
            }
        }
    }

    private val viewModel by activityViewModels<ContactsViewModel> {
        ViewModelFactory(requireActivity())
    }
    private lateinit var type: Contact.Type
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val args = requireArguments()
        type = args.getSerializable(ARG_CONTACT_TYPE) as Contact.Type

        val root = inflater.inflate(R.layout.fragment_contact_list, container, false)

        recycler = root.findViewById(R.id.contacts_list_recycler)
        val layoutManager = LinearLayoutManager(requireContext())
        recycler.layoutManager = layoutManager
        adapter = ContactAdapter(viewModel) { clickedPosition ->
            viewModel.onContactClicked(type, clickedPosition)
            adapter.notifyItemChanged(clickedPosition)
        }
        recycler.adapter = adapter

        val liveData = if (type == Contact.Type.PHONE) viewModel.phones else viewModel.emails
        liveData.observe(requireActivity(), { contactList ->
            if (contactList != null) {
                adapter.setData(contactList)
            }
        })

        if (type == Contact.Type.EMAIL) {
            root.findViewById<View>(R.id.contacts_list_email_select).setOnClickListener {
                viewModel.onSelectAccount()
            }

            if (viewModel.emailAccount.value.isNullOrEmpty()) {
                root.findViewById<View>(R.id.contacts_list_email_overlay).visibility = View.VISIBLE
            }

            viewModel.emailAccount.observe(requireActivity(), { email ->
                setOverlayVisible(email.isNullOrEmpty())
            })
        }

        return root
    }

    private fun setOverlayVisible(show: Boolean) {
        view?.run {
            val overlay = findViewById<View>(R.id.contacts_list_email_overlay)

            if (show) {
                recycler.visibility = View.GONE
                overlay.visibility = View.VISIBLE
            } else {
                recycler.visibility = View.VISIBLE
                overlay.visibility = View.GONE
            }
        }
    }
}