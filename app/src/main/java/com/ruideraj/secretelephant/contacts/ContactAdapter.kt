package com.ruideraj.secretelephant.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ruideraj.secretelephant.R

class ContactAdapter(private val fragment: Fragment,
                     private val viewModel: ContactsViewModel,
                     private val listener: ContactClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var contacts: List<Contact>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_list_item, parent, false)

        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contact = contacts!![position]

        val vh = holder as ViewHolder
        vh.name.text = contact.name
        vh.data.text = contact.data

        Glide.with(fragment)
                .load(contact.avatarUri)
                .placeholder(R.drawable.ic_contact_placeholder)
                .circleCrop()
                .into(vh.image)

        val colorId = if (viewModel.selectedContacts.contains(contact)) {
            R.color.bg_contact_selected
        } else {
            R.color.bg_activity
        }
        vh.itemView.setBackgroundColor(ContextCompat.getColor(vh.itemView.context, colorId))
    }

    override fun getItemCount() = contacts?.size ?: 0

    fun setData(data: List<Contact>) {
        contacts = data
        notifyDataSetChanged()
    }

    fun interface ContactClickListener {
        fun onContactClick(position: Int)
    }

    private class ViewHolder(itemView: View, listener: ContactClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val clickListener = listener
        val name: TextView = itemView.findViewById(R.id.contactName)
        val data: TextView = itemView.findViewById(R.id.contactData)
        val image: ImageView = itemView.findViewById(R.id.contactImage)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                clickListener.onContactClick(adapterPosition)
            }
        }
    }
}