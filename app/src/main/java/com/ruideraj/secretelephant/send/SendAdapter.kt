package com.ruideraj.secretelephant.send

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.ruideraj.secretelephant.R
import com.ruideraj.secretelephant.contacts.Contact

class SendAdapter(private val viewModel: SendViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var messages = emptyList<Message>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.send_list_item, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as ViewHolder
        val message = messages[position]

        val iconId = if (message.contact.type == Contact.Type.PHONE) {
            R.drawable.ic_chat_black_24dp
        } else {
            R.drawable.ic_email_black_24dp
        }
        vh.icon.setImageResource(iconId)

        vh.name.text = message.contact.name

        when(message.status) {
            Message.Status.SENT -> {
                vh.progress.visibility = View.GONE
                vh.refresh.visibility = View.GONE
                vh.status.setImageResource(R.drawable.ic_check_24dp)
                val successColor = ContextCompat.getColor(vh.status.context, R.color.send_success)
                ImageViewCompat.setImageTintList(vh.status, ColorStateList.valueOf(successColor))
                vh.buttonLayout.visibility = View.VISIBLE
            }
            Message.Status.ERROR -> {
                vh.progress.visibility = View.GONE
                vh.refresh.visibility = View.VISIBLE
                ImageViewCompat.setImageTintList(vh.status, null)
                vh.status.setImageResource(R.drawable.ic_warning_24dp)
                vh.buttonLayout.visibility = View.VISIBLE
            }
            Message.Status.IN_PROGRESS -> {
                vh.buttonLayout.visibility = View.GONE
                vh.progress.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = messages.size

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.send_icon)
        val name: TextView = itemView.findViewById(R.id.send_name)
        val progress: ProgressBar = itemView.findViewById(R.id.send_progress)
        val buttonLayout: LinearLayout = itemView.findViewById(R.id.send_button_layout)
        val refresh: ImageView = itemView.findViewById(R.id.send_refresh)
        val status: ImageView = itemView.findViewById(R.id.send_status)

        init {
            refresh.setOnClickListener { viewModel.resendMessage(adapterPosition) }
        }
    }
}



