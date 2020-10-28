package com.piso12.indoorex.services.firebase

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.piso12.indoorex.R
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.models.CurrentLocation
import com.piso12.indoorex.models.Notification
import java.time.LocalDateTime

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.containsKey("CONFIG_STATE")) {
            RemoteConfigService(this).fetchRemoteConfig()
        }
        if (message.data.containsKey("campaign_id")) {
            sendNotificationBroadcast(
                createNofitication(message)
            )
        }
    }

    private fun createNofitication(message: RemoteMessage): Notification {
        return Notification(
            message.data["campaign_id"]!!.toLong(),
            getDrawIdOrNull(message),
            message.data["title"]!!,
            message.data["body"]!!,
            message.data["image_url"]!!,
            LocalDateTime.now().toString(),
            false
        )
    }

    private fun getDrawIdOrNull(message: RemoteMessage): Long? {
        message.data["draw_id"]?.let {
            return it.toLong()
        }
        return null
    }

    private fun sendNotificationBroadcast(notification: Notification) {
        val intent = Intent("NOTIFICATION_EVENT")
        intent.putExtra("notification", notification)
        sendBroadcast(intent)
    }

    override fun onNewToken(newToken: String) {
        FirebaseMessaging.getInstance().subscribeToTopic("PUSH_RC")
        ContextHelper.setProperty(this, getString(R.string.firebase_auth_token_key), newToken)
    }
}
