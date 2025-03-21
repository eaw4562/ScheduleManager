package com.team.personalschedule_xml.ui.common.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.Calendar

class CurrentDayDecorator : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        // 매번 CalendarDay.today()를 호출하여 현재 날짜와 비교합니다.
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1  // 0-indexed 처리
        val day1 = calendar.get(Calendar.DAY_OF_MONTH)
        val today = CalendarDay.from(year, month, day1)
        return day == today
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CurrentDaySpan())
    }
}
