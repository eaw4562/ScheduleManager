package com.team.personalschedule_xml.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleItemAdapter(
    private val scheduleList : List<Schedule>,
    private val onItemClicked : (Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleItemAdapter.ScheduleViewHolder>(){

    inner class ScheduleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val startDateTextView : TextView = itemView.findViewById(R.id.bottom_sheet_item_startDate_text)
        val endDateTextView : TextView = itemView.findViewById(R.id.bottom_sheet_item_endDate_text)
        val colorImageView : ImageView = itemView.findViewById(R.id.bottom_sheet_item_color_img)
        val contentTextView : TextView = itemView.findViewById(R.id.bottom_sheet_item_content_text)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScheduleItemAdapter.ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_bottom_sheet_item, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleItemAdapter.ScheduleViewHolder, position: Int) {
        val schedule = scheduleList[position]

        val startLdt = LocalDateTime.parse(schedule.startDateTime)
        val endLdt = LocalDateTime.parse(schedule.endDateTime)

        val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)

        holder.startDateTextView.text = startLdt.format(formatter)
        holder.endDateTextView.text = endLdt.format(formatter)
        holder.colorImageView.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, schedule.labelColor)
        )
        holder.contentTextView.text = schedule.title

        holder.itemView.setOnClickListener {
            onItemClicked(schedule)
        }
    }

    override fun getItemCount(): Int = scheduleList.size

}