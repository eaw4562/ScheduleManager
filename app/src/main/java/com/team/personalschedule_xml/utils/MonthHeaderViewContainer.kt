package com.team.personalschedule_xml.utils

import android.view.View
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.team.personalschedule_xml.databinding.CalendarDayLayoutBinding
import com.team.personalschedule_xml.databinding.CalendarMonthHeaderLayoutBinding

class MonthHeaderViewContainer(view: View) : ViewContainer(view) {
    val binding = CalendarMonthHeaderLayoutBinding.bind(view)
    lateinit var day: CalendarDay
}
