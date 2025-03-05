package com.team.personalschedule_xml.ui.schedule

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.FragmentScheduleBottomSheetFragmentBinding
import java.time.LocalDate

class ScheduleBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentScheduleBottomSheetFragmentBinding

    companion object {
        private const val ARG_DATE = "arg_date"
        fun newInstance(data : LocalDate) : ScheduleBottomSheetFragment {
            val fragment = ScheduleBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_DATE, data.toString())
            fragment.arguments = args
            return fragment
        }
    }

   /* override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(it)
                behavior.skipCollapsed = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
            return dialog
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("BottomSheet", "show")
        binding = FragmentScheduleBottomSheetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { d ->
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

}
