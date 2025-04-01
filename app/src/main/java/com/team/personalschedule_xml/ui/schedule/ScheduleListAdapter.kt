package com.team.personalschedule_xml.ui.schedule

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleListAdapter : ListAdapter<ScheduleListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SCHEDULE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ScheduleListItem.Header -> TYPE_HEADER
            is ScheduleListItem.ScheduleItem -> TYPE_SCHEDULE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_schedule_list_header, parent, false)
            HeaderViewHolder(view)
        }else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_schedule_list_item, parent, false)
            ScheduleViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ScheduleListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ScheduleListItem.ScheduleItem -> (holder as ScheduleViewHolder).bind(item.schedule)
        }
    }

    class HeaderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val dateText : TextView = itemView.findViewById(R.id.tvScheduleDate)
        private val todayLabel: TextView = itemView.findViewById(R.id.tvScheduleToday)

        fun bind(item: ScheduleListItem.Header) {
            dateText.text = item.date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)"))
            todayLabel.visibility = if (item.isToday) View.VISIBLE else View.GONE
        }
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStartTime: TextView = itemView.findViewById(R.id.tvScheduleStartTime)
        private val tvEndTime: TextView = itemView.findViewById(R.id.tvScheduleEndTime)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvScheduleTitle)
        private val alarmIcon: ImageView = itemView.findViewById(R.id.tvScheduleAlarm)
        private val colorBar: ImageView = itemView.findViewById(R.id.bottom_sheet_item_color_img)

        fun bind(schedule: Schedule) {
            val date = schedule.startDateTime?.toLocalDate()
            val startTime = schedule.startDateTime?.toLocalTime()
            val endTime = schedule.endDateTime?.toLocalTime()

            tvStartTime.text = if (schedule.isAllDay) "종일" else startTime?.format(DateTimeFormatter.ofPattern("a h:mm"))
            tvEndTime.text = if (schedule.isAllDay) "" else endTime?.format(DateTimeFormatter.ofPattern("a h:mm"))
            tvTitle.text = schedule.title

            tvEndTime.visibility = if (schedule.isAllDay) View.GONE else View.VISIBLE

            // 컬러 바
            val color = ContextCompat.getColor(itemView.context, schedule.labelColor)
            colorBar.setBackgroundColor(color)

            // 알람 아이콘 가시성
            alarmIcon.visibility = if (schedule.alarm.isNullOrEmpty() || schedule.alarm == "알림 없음") View.GONE else View.VISIBLE

            Log.d("ScheduleListAdapter save Date: ", date.toString())
            Log.d("ScheduleListAdapter Today: ", LocalDate.now().toString())
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<ScheduleListItem>() {
    override fun areItemsTheSame(oldItem: ScheduleListItem, newItem: ScheduleListItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ScheduleListItem, newItem: ScheduleListItem): Boolean {
        return oldItem == newItem
    }
}
