package com.team.personalschedule_xml.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.team.personalschedule_xml.data.repository.HolidayRepository
import com.team.personalschedule_xml.ui.schedule.ScheduleViewModel

class ScheduleViewModelFactory(private val repository: HolidayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}