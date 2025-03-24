package com.team.personalschedule_xml.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.team.personalschedule_xml.data.model.ActionType
import com.team.personalschedule_xml.data.model.Notification
import com.team.personalschedule_xml.databinding.LayoutNotificationItemBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class NotificationAdapter(
    private var notifications : List<Notification>,
    private val onItemClick : (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding : LayoutNotificationItemBinding) :
    RecyclerView.ViewHolder(binding.root){

        fun bind(notification : Notification) {
            binding.apply {
                notificationTitle.text = notification.title
                notificationLabel.setBackgroundColor(ContextCompat.getColor(root.context, notification.labelColor))
                notificationDateTime.text = formatDateTime(notification.startDateTime, notification.endDateTime)
                notificationMessage.text = getActionMessage(notification.actionType)
                /**
                 * Type mismatch.
                 * Required:
                 * String
                 * Found:
                 * LocalDateTim
                 */
                notificationCreatedAt.text = formatRelativeDateTime(notification.createdAt)

                root.setOnClickListener {
                    onItemClick(notification)
                }
            }
        }

        private fun formatDateTime(start: LocalDateTime?, end: LocalDateTime?) : String {
            if (start == null || end == null) return ""

            val formatterOutput = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) a h:mm", Locale.KOREAN)

            val startFormatted = start.format(formatterOutput)
            val endFormatted = end.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))

            return "$startFormatted - $endFormatted"
        }

        private fun getActionMessage(type : ActionType) : String {
            return when (type) {
                ActionType.CREATED -> "일정을 작성했습니다."
                ActionType.UPDATED -> "일정을 수정했습니다."
                ActionType.DELETED -> "일정을 삭제했습니다."
                ActionType.COPIED -> "일정을 복사했습니다."
                //ActionType.COMMENTED -> "일정에 댓글을 추가했습니다."
                //ActionType.LIKED -> "좋아요를 눌렀습니다."
             }
        }

        private fun formatRelativeDateTime(dateTime: LocalDateTime): String {
            val now = LocalDateTime.now()

            val daysDiff = ChronoUnit.DAYS.between(dateTime.toLocalDate(), now.toLocalDate())
            return when {
                daysDiff == 0L -> "오늘, ${dateTime.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))}"
                daysDiff == 1L -> "어제, ${dateTime.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN))}"
                else -> dateTime.format(DateTimeFormatter.ofPattern("M.d, a h:mm", Locale.KOREAN))
            }
        }
    }

    fun updateNotifications(newNotifications : List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = LayoutNotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }
}