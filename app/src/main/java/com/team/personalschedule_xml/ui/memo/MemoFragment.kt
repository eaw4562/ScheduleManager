package com.team.personalschedule_xml.ui.memo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.LayoutMemoBinding

class MemoFragment : Fragment() {

    private var _binding : LayoutMemoBinding? = null
    private val binding get() = _binding!!
    private val memoViewModel : MemoViewModel by activityViewModels()
    private lateinit var adapter: MemoGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = LayoutMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MemoGridAdapter { memo ->
            val bundle = Bundle().apply {
                putInt("scheduleId", memo.id)
                putBoolean("isMemo", true)
            }

            findNavController().navigate(R.id.action_memoFragment_to_scheduleDetailFragment, bundle)
        }
        binding.memoRecycler.adapter = adapter
        binding.memoRecycler.layoutManager = GridLayoutManager(requireContext(), 2)

        memoViewModel.memoList.observe(viewLifecycleOwner) { memos ->
            Log.d("MemoFragment", "Loaded memos: ${memos.size}")
            adapter.submitList(memos)
        }
    }
}