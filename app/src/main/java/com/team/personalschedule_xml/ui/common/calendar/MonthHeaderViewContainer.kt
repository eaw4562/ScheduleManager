package com.team.personalschedule_xml.ui.common.calendar

import android.view.View
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.team.personalschedule_xml.databinding.LayoutCalendarMonthHeaderBinding

class MonthHeaderViewContainer(view: View) : ViewContainer(view) {
    val binding = LayoutCalendarMonthHeaderBinding.bind(view)
    lateinit var day: CalendarDay
}
