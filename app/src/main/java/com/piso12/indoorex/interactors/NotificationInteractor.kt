package com.piso12.indoorex.interactors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.UserCampaignDto
import com.piso12.indoorex.exceptions.campaign.CampaignExceptionHandler
import com.piso12.indoorex.fragments.notification.NotificationFragment
import com.piso12.indoorex.fragments.notification.NotificationPopupFragment
import com.piso12.indoorex.models.CurrentLocation
import com.piso12.indoorex.models.Notification
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.notification.NotificationService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.NotificationViewModel
import kotlinx.android.synthetic.main.feed_fragment.*

object NotificationInteractor {

    private lateinit var mContext: FragmentActivity
    private lateinit var mNotificationService: NotificationService
    private lateinit var mBroadcastReceiver: NotificationReceiver
    private lateinit var mNotificationViewModel: NotificationViewModel
    private const val TAG = "NotificationInteractor"

    fun addNotification(notification: Notification) {
        mNotificationService.addNotification(notification)
        mNotificationViewModel.mNotifications.postValue(mNotificationService.getNotifications())
    }

    fun openNotification(notification: Notification) {
        notification.campaignId?.let { campaignId ->
            mNotificationService.postOpenedNotification(
                campaignId,
                AuthService.getCurrentUser().uid,
                onPostNotificationOpenedCallback())
        }
        mNotificationService.markAsRead(notification)
        mNotificationViewModel.mNotifications.postValue(mNotificationService.getNotifications())
        mNotificationViewModel.mIncomingNotification.postValue(notification)
    }

    private fun onPostNotificationOpenedCallback(): ServiceCallback<UserCampaignDto> {
        return object : ServiceCallback<UserCampaignDto> {
            override fun onComplete(result: ServiceResult<UserCampaignDto>) {
                if (!result.isSuccessful) {
                    CampaignExceptionHandler.getInstance(mContext)
                        .showError(mContext.feed_container,
                            TAG, result.error!!)
                }
            }
        }
    }

    fun showNotifications() {
       mContext.supportFragmentManager?.beginTransaction()!!
            .setCustomAnimations(
                R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom)
            .add(R.id.feed_container, NotificationFragment())
            .addToBackStack(null)
            .commit()
    }

    fun fetchNotifications() {
        val notifications = mNotificationService.getNotifications()
        mNotificationViewModel.mNotifications.postValue(notifications)
    }

    private fun showNotificationAlert(notification: Notification) {
        val notificationFragment = NotificationPopupFragment()
        val args = Bundle()
        args.putSerializable("notification", notification)
        notificationFragment.arguments = args
        mContext.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.anim_slide_down_to_origin, R.anim.anim_slide_up_from_origin)
            .add(R.id.container, notificationFragment)
            .commit()
    }

    fun unregisterBroadcastReceiver() {
        mContext.unregisterReceiver(mBroadcastReceiver)
    }

    private class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "NOTIFICATION_EVENT" -> {
                    val notification =
                        intent.getSerializableExtra("notification") as Notification
                    addNotification(notification)
                    showNotificationAlert(notification)
                }
            }
        }
    }

    fun initialize(context: FragmentActivity) {
        mContext = context
        mBroadcastReceiver = NotificationReceiver()
        mContext.registerReceiver(
            mBroadcastReceiver,
            IntentFilter().also { it.addAction("NOTIFICATION_EVENT") }
        )
        mNotificationService = NotificationService(context)
        mNotificationViewModel = ViewModelProviders.of(context).get(NotificationViewModel::class.java)
    }
}
