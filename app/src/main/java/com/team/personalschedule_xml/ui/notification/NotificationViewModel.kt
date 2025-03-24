package com.team.personalschedule_xml.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.team.personalschedule_xml.data.model.Notification
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : ScheduleRepository = ScheduleRepository(application)

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    fun loadNotifications() {
        viewModelScope.launch {
            _notifications.value = repository.getAllNotifications()
        }
    }
}