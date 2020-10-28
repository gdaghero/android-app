package com.piso12.indoorex.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piso12.indoorex.models.CurrentLocation
import com.piso12.indoorex.models.Notification

class NotificationViewModel : ViewModel() {

    var mNotifications = MutableLiveData<List<Notification>>()
    var mIncomingNotification = MutableLiveData<Notification>()

    init {
        mNotifications.value = emptyList()
    }
}