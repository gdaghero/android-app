package com.piso12.indoorex.models

import java.io.Serializable

data class Notification(

    var campaignId: Long,
    var drawId: Long?,
    var title: String,
    var body: String,
    var imageUrl: String,
    var date: String,
    var isRead: Boolean
) : Serializable