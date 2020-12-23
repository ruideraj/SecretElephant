package com.ruideraj.secretelephant.contacts

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ContactsPagerAdapter(fragmentActivity: FragmentActivity,
                           private val viewModel: ContactsViewModel)
    : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int)
            = ContactListFragment.newInstance(viewModel.getPageType(position))

    override fun getItemCount() = viewModel.pageCount
}