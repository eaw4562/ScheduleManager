package com.team.personalschedule_xml.ui.schedule

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.databinding.LayoutWeekDayItemBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class WeekAdapter(
    private val calendarViewModel: CalendarViewModel,
    private val onDayClick: (LocalDate, Boolean) -> Unit // isSelected 상태를 함께 전달
) : ListAdapter<LocalDate, WeekAdapter.WeekViewHolder>(DayDiffCallback()) {

    private val dayFormatter = DateTimeFormatter.ofPattern("d", Locale.KOREAN)
    private val weekdayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.KOREAN)
    private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)

    class WeekViewHolder(
        private val binding: LayoutWeekDayItemBinding,
        private val onDayClick: (LocalDate, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: LocalDate, isSelected: Boolean, schedules: List<Schedule>) {
            binding.dayText.text = date.format(DateTimeFormatter.ofPattern("d", Locale.KOREAN))
            binding.weekdayText.text = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.KOREAN))
            binding.isSelected = isSelected

            // 일정 표시
            binding.scheduleContainer.removeAllViews()

            schedules.forEach { schedule ->
                val scheduleLayout = LinearLayout(binding.root.context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(0, 4, 0, 4)
                }

                val colorBar = View(binding.root.context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        4.dp,
                        16.dp
                    ).apply {
                        setMargins(0, 0, 8.dp, 0)
                    }
                    /*backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(binding.root.context, schedule.labelColor)
                    )*/
                    setBackgroundColor(ContextCompat.getColor(binding.root.context, schedule.labelColor))
                    //setBackgroundResource(R.drawable.color_bar)
                    Log.d("WeekAdapter", backgroundTintList.toString())
                }

                val scheduleText = TextView(binding.root.context).apply {
                    text = if (schedule.isAllDay) {
                        "종일 ${schedule.title}"
                    } else {
                        "${schedule.startDateTime?.toLocalTime()?.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))} ${schedule.title}"
                    }
                    setTextColor(Color.BLACK)
                    textSize = 14f
                }
                scheduleLayout.addView(colorBar)
                scheduleLayout.addView(scheduleText)

                binding.scheduleContainer.addView(scheduleLayout)
            }

            binding.root.setOnClickListener {
                onDayClick(date, isSelected) // isSelected 상태를 함께 전달
            }
        }

        private val Int.dp : Int
            get() = (this * binding.root.context.resources.displayMetrics.density).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val binding = LayoutWeekDayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeekViewHolder(binding, onDayClick)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val date = getItem(position)
        val isSelected = date == calendarViewModel.selectedStartDate.value
        val schedules = calendarViewModel.scheduleMap.value?.get(date) ?: emptyList()
        holder.bind(date, isSelected, schedules)
    }
}

class DayDiffCallback : DiffUtil.ItemCallback<LocalDate>() {
    override fun areItemsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LocalDate, newItem: LocalDate): Boolean {
        return oldItem == newItem
    }
}