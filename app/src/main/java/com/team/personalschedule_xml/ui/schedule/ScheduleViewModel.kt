package com.team.personalschedule_xml.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.personalschedule_xml.data.model.Holiday
import com.team.personalschedule_xml.repository.HolidayRepository
import com.team.personalschedule_xml.utils.DateUtil
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class ScheduleViewModel(private val repository: HolidayRepository) : ViewModel() {

    private val _holidayMap = MutableLiveData<Map<LocalDate, String>>()
    val holidayMap: LiveData<Map<LocalDate, String>> = _holidayMap

    fun fetchHolidays(year: Int) {
        viewModelScope.launch {
            val response = repository.getHolidays(year)
            val holidays: List<Holiday> = response?.response?.body?.items?.item ?: emptyList()
            response?.response?.body?.items?.item ?: emptyList()
            val map = holidays.mapNotNull { holiday ->
                DateUtil.parseLocdate(holiday.locdate)?.let { calendarDay ->
                    // calendarDay.date가 java.util.Date라면 getTime()을 사용하여 밀리초 값을 얻습니다.
                    val localDate = Instant.ofEpochMilli(calendarDay.date.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    localDate to holiday.dateName
                }
            }.toMap()
            _holidayMap.value = map
        }
    }
}