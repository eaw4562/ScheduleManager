package com.team.personalschedule_xml.ui.memo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team.personalschedule_xml.data.model.Memo
import com.team.personalschedule_xml.databinding.ItemMemoGridBinding

class MemoGridAdapter(
    private val onMemoClick : (Memo) -> Unit
) : RecyclerView.Adapter<MemoGridAdapter.MemoViewHolder>() {

    private val memoList = mutableListOf<Memo>()

    fun submitList(list: List<Memo>) {
        memoList.clear()
        memoList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MemoViewHolder(
        private val binding : ItemMemoGridBinding,) : RecyclerView.ViewHolder(binding.root) {
        fun bind(memo: Memo) {
            binding.textTitle.text = memo.title
            binding.textTitle.setTextColor(ContextCompat.getColor(binding.root.context, memo.labelColorResId))

            binding.root.setOnClickListener {
                onMemoClick(memo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(memoList[position])
    }

    override fun getItemCount(): Int = memoList.size
}