package com.piso12.indoorex.services.notification

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.piso12.indoorex.dtos.UserCampaignDto
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.models.Notification
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.notification.callbacks.OnPostNotificationOpenedCallback
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.contracts.INotificationService
import com.piso12.indoorex.services.utils.ServiceCallback
import retrofit2.create

class NotificationService(context: Context) {

    private val mContext = context

    fun addNotification(notification: Notification) {
        val notifications = getNotifications().toMutableList()
        notifications.add(notification)
        saveNotifications(notifications)
    }

    private fun saveNotifications(notifications: List<Notification>) {
        val key = AuthService.getCurrentUser().uid
        ContextHelper.setProperty(mContext, key, Gson().toJson(notifications))
    }

    fun getNotifications(): List<Notification> {
        val key = AuthService.getCurrentUser().uid
        var notifications = listOf<Notification>()
        try {
            notifications = JsonParser<Notification>()
                .parseAsList(ContextHelper.getProperty(mContext, key),
                    object : TypeToken<List<Notification>>(){}.type)
        } catch (ex: IllegalStateException) {
        } finally {
            return notifications.sortedByDescending { it.date }
        }
    }

    fun markAsRead(notification: Notification) {
        val notifications = getNotifications().toMutableList()
        notifications.forEach {
            if (it.date == notification.date) {
                it.isRead = true
            }
        }
        saveNotifications(notifications)
    }

    fun postOpenedNotification(campaignId: Long, userId: String,
                         callback: ServiceCallback<UserCampaignDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<INotificationService>()
            .postOpenedNotification(campaignId, userId)
            .enqueue(OnPostNotificationOpenedCallback(callback))
    }
}