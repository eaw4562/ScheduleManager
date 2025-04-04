package com.team.personalschedule_xml.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.LayoutSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding : LayoutSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = LayoutSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }
}