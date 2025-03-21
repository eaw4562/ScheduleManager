package com.team.personalschedule_xml.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.personalschedule_xml.data.model.Holiday
import com.team.personalschedule_xml.data.repository.HolidayRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(private val repository: HolidayRepository) : ViewModel() {

    private val _holidayMap = MutableLiveData<Map<LocalDate, String>>()
    val holidayMap: LiveData<Map<LocalDate, String>> = _holidayMap

    fun fetchHolidays(year: Int) {
        viewModelScope.launch {
            val response = repository.getHolidays(year)
            val holidays: List<Holiday> = response?.response?.body?.items?.item ?: emptyList()
            val map = holidays.mapNotNull { holiday ->
                val holidayDateStr = holiday.locdate.toString() // 예: "20250101"
                if (holidayDateStr.length == 8) {
                    val year = holidayDateStr.substring(0, 4).toInt()
                    val month = holidayDateStr.substring(4, 6).toInt()
                    val day = holidayDateStr.substring(6, 8).toInt()
                    // LocalDate.of()는 month가 1~12임에 주의
                    val localDate = LocalDate.of(year, month, day)
                    localDate to holiday.dateName
                } else {
                    null
                }
            }.toMap()
            _holidayMap.value = map
        }
    }
}