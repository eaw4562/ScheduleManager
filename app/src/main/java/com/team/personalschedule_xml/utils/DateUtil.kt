package com.team.personalschedule_xml.utils

import com.prolificinteractive.materialcalendarview.CalendarDay

object DateUtil {
    fun parseLocdate(locdate : Int) : CalendarDay? {
        val year = locdate / 10000
        val month = (locdate % 10000) / 100
        val day = locdate % 100
        return try {
            CalendarDay.from(year,month, day)
        }catch (e: Exception) {
            null
        }
    }
}