package com.ruideraj.secretelephant.match

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ruideraj.secretelephant.Mode
import com.ruideraj.secretelephant.R

class MatchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var exchange: MatchExchange? = null
    private var matchesShown: BooleanArray? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.match_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ex = exchange!!
        val vh = holder as ViewHolder
        val contact = ex.contacts[position]

        vh.participant.text = contact.name

        val showMatch = matchesShown!![position]
        if (showMatch) {
            if (ex.mode == Mode.SANTA) {
                val recipient = ex.contacts[ex.matches[position]]
                vh.recipient.text = recipient.name
            } else {
                val orderPosition = ex.matches[position] + 1 // + 1 to account for zero-indexing
                vh.recipient.text = "$orderPosition"
            }

            vh.recipient.setTypeface(null, Typeface.NORMAL)
        } else {
            vh.recipient.text = "?"
            vh.recipient.setTypeface(null, Typeface.BOLD)
        }
    }

    override fun getItemCount() = exchange?.contacts?.size ?: 0

    fun setData(ex: MatchExchange) {
        exchange = ex
        matchesShown = BooleanArray(ex.contacts.size)
        notifyDataSetChanged()
    }

    fun setNewMatches(ex: MatchExchange) {
        exchange = ex
        notifyItemRangeChanged(0, ex.contacts.size)
    }

    private inner class ViewHolder(itemView: View,
                             val participant: TextView = itemView.findViewById(R.id.match_name),
                             val recipient: TextView = itemView.findViewById(R.id.match_recipient))
        : RecyclerView.ViewHolder(itemView) {

        init {
            recipient.setOnClickListener {
                matchesShown?.let {
                    val position = adapterPosition
                    it[position] = !it[position]
                    notifyItemChanged(position)
                }
            }
        }
    }
}

