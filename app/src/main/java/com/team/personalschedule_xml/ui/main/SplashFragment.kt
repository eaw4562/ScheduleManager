package com.team.personalschedule_xml.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.utils.PreferencesUtil

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SharedPreferences에서 시작 프래그먼트 값을 읽습니다.
        val startDest = PreferencesUtil.getStartDestination(requireContext())
        val actionId = when (startDest) {
            "month" -> R.id.action_splash_to_scheduleFragment
            "week" -> R.id.action_splash_to_weekFragment
            "list" -> R.id.action_splash_to_listFragment
            else -> R.id.action_splash_to_scheduleFragment
        }
        Log.d("SplashFragment", startDest)
        findNavController().navigate(actionId)
    }
}