package com.team.personalschedule_xml.ui.common.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.CalendarLabel

class CalendarLabelAdapter(
    private val labels: MutableList<CalendarLabel>,
    private val onLabelSelected: (CalendarLabel) -> Unit
) : RecyclerView.Adapter<CalendarLabelAdapter.CalendarLabelViewHolder>() {

    inner class CalendarLabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorImage: ImageView = itemView.findViewById(R.id.labelColorImage)
        val colorNameText: TextView = itemView.findViewById(R.id.labelNameText)
        val radioButton: RadioButton = itemView.findViewById(R.id.labelRadioButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarLabelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_calendar_label_item, parent, false)
        return CalendarLabelViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CalendarLabelViewHolder, position: Int) {
        val label = labels[position]

        // 색상 이미지의 배경을 해당 색상으로 설정
        holder.colorImage.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, label.color)
        )
        holder.colorNameText.text = label.colorName
        holder.radioButton.isChecked = label.isSelected

        holder.radioButton.setOnClickListener {
            labels.forEach { it.isSelected = false }
            label.isSelected = true
            onLabelSelected(label)
            notifyDataSetChanged()
        }


        // 아이템 클릭 시 단일 선택 처리
        holder.itemView.setOnClickListener {
            // 모든 항목 unselect 처리 후 현재 항목만 선택
            labels.forEach { it.isSelected = false }
            label.isSelected = true
            onLabelSelected(label)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = labels.size
}