package com.team.personalschedule_xml.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.databinding.LayoutScheduleSearchBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import com.team.personalschedule_xml.ui.schedule.ScheduleListItem
import java.time.LocalDate

class ScheduleSearchFragment : Fragment() {

    private var _binding : LayoutScheduleSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel : CalendarViewModel by activityViewModels()
    private lateinit var adapter: SearchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding =  LayoutScheduleSearchBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchListAdapter { searchItem ->
            val scheduleId = searchItem.schedule.id

            val action = ScheduleSearchFragmentDirections.actionScheduleSearchFragmentToScheduleDetailFragment(scheduleId)
            findNavController().navigate(action)
        }
        binding.rvSearchHistory.adapter = adapter
        binding.rvSearchHistory.layoutManager = LinearLayoutManager(requireContext())

        binding.searchBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchClearLayout.setOnClickListener {
            adapter.clear()
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setSearchKeyword(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        viewModel.filteredSchedules.observe(viewLifecycleOwner) { groupedSchedules ->
            adapter.submitList(transformScheduleList(groupedSchedules))
        }
    }

    private fun transformScheduleList(scheduleMap: Map<LocalDate, List<Schedule>>) : List<SearchListItem> {
        val list = mutableListOf<SearchListItem>()
        val sortedMap = scheduleMap.toSortedMap()

        sortedMap.forEach { (date, schedules) ->
            list.add(SearchListItem.Header(date, schedules.size))
            schedules.sortedBy { it.startDateTime }.forEach { schedule ->
                list.add(SearchListItem.SearchItem(schedule))
            }
        }
        return list
    }
}