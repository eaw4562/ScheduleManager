package com.team.personalschedule_xml.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.ui.schedule.DiffCallback
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchListAdapter(
    private val onItemClick : (SearchListItem.SearchItem) -> Unit
) : ListAdapter<SearchListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchListItem.Header -> TYPE_HEADER
            is SearchListItem.SearchItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_header, parent, false))
            TYPE_ITEM -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_history, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(getItem(position) as SearchListItem.Header)
            is ItemViewHolder -> holder.bind(getItem(position) as SearchListItem.SearchItem)
        }
    }

    fun clear() {
        submitList(emptyList())
    }


    inner class HeaderViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val tvDate : TextView = view.findViewById(R.id.tvDate)
        private val tvCount : TextView = view.findViewById(R.id.tvCount)

        fun bind(header : SearchListItem.Header) {
            tvDate.text = header.date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)", Locale.KOREAN))
            tvCount.text = "${header.itemCount}건의 일정"
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle : TextView = view.findViewById(R.id.tvTitle)
        private val tvTime : TextView = view.findViewById(R.id.tvTime)
        private val colorBar : View = view.findViewById(R.id.colorBar)

        fun bind(item : SearchListItem.SearchItem) {
            val schedule = item.schedule
            tvTitle.text = schedule.title
            tvTime.text = "${schedule.startDateTime?.toLocalTime()} - ${schedule.endDateTime?.toLocalTime()}"
            colorBar.setBackgroundColor(ContextCompat.getColor(itemView.context, schedule.labelColor))

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SearchListItem>() {
        override fun areItemsTheSame(oldItem: SearchListItem, newItem: SearchListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SearchListItem, newItem: SearchListItem): Boolean {
            return oldItem == newItem
        }

    }
}