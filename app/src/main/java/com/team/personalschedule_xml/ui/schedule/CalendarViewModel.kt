package com.team.personalschedule_xml.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime

class CalendarViewModel : ViewModel() {
    // 선택된 날짜를 LiveData로 관리
    private val _selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _selectedTime = MutableLiveData<LocalTime>(LocalTime.now())
    val selectedTime : LiveData<LocalTime> = _selectedTime

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectTime(time: LocalTime) {
        _selectedTime.value = time
    }
}