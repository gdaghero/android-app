package com.piso12.indoorex.fragments.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.piso12.indoorex.R
import com.piso12.indoorex.adapters.notification.NotificationsRecyclerAdapter
import com.piso12.indoorex.interactors.NotificationInteractor
import com.piso12.indoorex.models.Notification
import com.piso12.indoorex.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.notification_fragment.*

class NotificationFragment : Fragment() {

    private lateinit var mNotificationViewModel: NotificationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              state: Bundle?): View? {
        return inflater.inflate(R.layout.notification_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        loadNotifications()
        handleClose()
    }

    private fun handleClose() {
        ib_close_notifications.setOnClickListener {
            fragmentManager?.let {
                it.popBackStack()
            }
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mNotificationViewModel = ViewModelProviders.of(it).get(NotificationViewModel::class.java)

        }
        observeNotifications()
    }

    private fun observeNotifications() {
        mNotificationViewModel.mNotifications.observe(this, Observer { notifications ->
            (rv_notifications.adapter as NotificationsRecyclerAdapter)
                .setNotifications(notifications)
        })
    }

    private fun loadNotifications() {
        val adapter =
            NotificationsRecyclerAdapter(
                emptyList(),
                onNotificationClick()
            )
        adapter.setHasStableIds(true)
        rv_notifications.setHasFixedSize(true)
        rv_notifications.layoutManager = LinearLayoutManager(context)
        rv_notifications.adapter = adapter
    }

    private fun onNotificationClick(): OnClickListener {
        return object : OnClickListener {
            override fun onClick(notification: Notification) {
                NotificationInteractor.openNotification(notification)
                fragmentManager?.popBackStack()
            }
        }
    }

    interface OnClickListener {
        fun onClick(notification: Notification)
    }
}
