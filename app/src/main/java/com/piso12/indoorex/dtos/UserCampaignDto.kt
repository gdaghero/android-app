package com.piso12.indoorex.dtos

import java.io.Serializable

data class UserCampaignDto(
    val userId: String,
    val campaignId: Long,
    val isLiked: Boolean,
    val isDisliked: Boolean,
    val isUsed: Boolean,
    val isOpened: Boolean
) : Serializable