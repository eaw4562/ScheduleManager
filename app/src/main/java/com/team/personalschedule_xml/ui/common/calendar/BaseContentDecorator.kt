package com.team.personalschedule_xml.ui.common.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

/**
 * BaseContentDecorator:
 * 모든 날짜 셀에 BaseContentSpan을 적용하여 날짜 텍스트 아래에 고정 높이의 영역을 확보
 */
class BaseContentDecorator(private val extraSpace: Int) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return true  // 모든 날짜에 적용
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(BaseContentSpan(extraSpace))
    }
}