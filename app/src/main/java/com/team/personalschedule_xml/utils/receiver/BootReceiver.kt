package com.team.personalschedule_xml.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import com.team.personalschedule_xml.utils.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED || context == null) {
            Log.w("BootReceiver", "Invalid context or intent action")
            return
        }

        val repository = ScheduleRepository(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val schedules = repository.getAllSchedules()
                schedules.forEach { schedule ->
                    AlarmScheduler.scheduleAlarms(context, schedule.id, schedule)
                }
                Log.d("BootReceiver", "Successfully rescheduled ${schedules.size} alarms after boot")
            } catch (e: Exception) {
                Log.e("BootReceiver", "Failed to reschedule alarms after boot: ${e.message}", e)
            }
        }
    }
}