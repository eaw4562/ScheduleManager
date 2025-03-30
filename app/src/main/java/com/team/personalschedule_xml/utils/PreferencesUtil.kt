package com.team.personalschedule_xml.utils

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtil {
    private const val PREFS_NAME = "app_preferences"
    private const val KEY_START_DESTINATION = "start_destination"

    private fun getPreferences(context: Context) : SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setStartDestination(context: Context, destination : String) {
        getPreferences(context).edit()
            .putString(KEY_START_DESTINATION, destination)
            .apply()
    }

    fun getStartDestination(context: Context, default: String = "month"): String {
        return getPreferences(context).getString(KEY_START_DESTINATION, default) ?: default
    }}