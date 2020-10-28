package com.piso12.indoorex.adapters.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.fragments.notification.NotificationFragment
import com.piso12.indoorex.models.Notification
import kotlinx.android.synthetic.main.notification_item.view.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class NotificationsRecyclerAdapter(notifications: List<Notification>,
                                   callback: NotificationFragment.OnClickListener)
    : RecyclerView.Adapter<NotificationsRecyclerAdapter.NotificationViewHolder>() {

    private var mNotifications = notifications
    private val mCallback = callback

    fun setNotifications(notification: List<Notification>) {
        mNotifications = notification
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NotificationViewHolder(
            inflater.inflate(R.layout.notification_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mNotifications.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(mNotifications[position])
    }

    inner class NotificationViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val mView = item

        fun bind(notification: Notification) {
            mView.iv_notification_item_store_logo.loadLogo(notification.imageUrl)
            mView.tv_notification_item_title.text = notification.title
            mView.tv_notification_item_body.text = notification.body
            if (!notification.isRead) {
                mView.iv_notification_item_unread.visibility = View.VISIBLE
            }
            mView.tv_notification_item_date.text = calculateDateTimeAgo(notification.date)
            mView.setOnClickListener { mCallback.onClick(notification) }
        }

        private fun ImageView.loadLogo(url: String) {
            Glide
                .with(context)
                .load(url)
                .circleCrop()
                .into(this)
        }
    }

    private fun calculateDateTimeAgo(date: String): String {
        val fromDate = LocalDateTime.parse(date)
        val toDate = LocalDateTime.now()
        val millisBetween= ChronoUnit.MILLIS.between(fromDate, toDate)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisBetween)
        if (seconds < 60) {
            return "${seconds}s"
        }
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisBetween)
        if (minutes < 60) {
            return "${minutes}m"
        }
        val hours = TimeUnit.MILLISECONDS.toHours(millisBetween)
        if (hours < 24) {
            return "${hours}h"
        }
        val days = TimeUnit.MILLISECONDS.toDays(millisBetween)
        if (days < 7) {
            return "${days}d"
        }
        val weeks = ChronoUnit.WEEKS.between(fromDate, toDate)
        return "${weeks}d"
    }
}
