package com.ruideraj.secretelephant.contacts

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ruideraj.secretelephant.R

class ContactsInputAdapter(private val viewModel: ContactsViewModel,
                           private val contacts: List<Contact>,
                           private val textWatcher: TextWatcher)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CONTACT = 0
        private const val TYPE_EDIT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = if (viewType == TYPE_CONTACT) R.layout.contact_input_list_item
                        else R.layout.contact_input_list_edit
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        if (viewType == TYPE_EDIT) {
            view.findViewById<EditText>(R.id.contact_edit).addTextChangedListener(textWatcher)
        }

        return ViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder

        val type = getItemViewType(position)
        if (type == TYPE_CONTACT) {
            vh.name?.text = contacts[position].name
        } else if (type == TYPE_EDIT) {
            vh.edit?.setText(viewModel.filterText)
        }
    }

    override fun getItemCount() = contacts.size + 1  // +1 to include the name search EditText

    override fun getItemViewType(position: Int) = if (position == contacts.size) {
        TYPE_EDIT
    } else {
        TYPE_CONTACT
    }

    private class ViewHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null
        var edit: EditText? = null

        init {
            if (type == TYPE_CONTACT) {
                name = itemView as TextView
            } else {
                edit = itemView.findViewById(R.id.contact_edit)
            }
        }
    }
}