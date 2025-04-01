package com.team.personalschedule_xml.ui.schedule

import com.team.personalschedule_xml.data.model.Schedule
import java.time.LocalDate

sealed class ScheduleListItem {
    data class Header(val date: LocalDate, val isToday: Boolean) : ScheduleListItem()
    data class ScheduleItem(val schedule: Schedule) : ScheduleListItem()
}