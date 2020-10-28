package com.piso12.indoorex.services.campaign

import android.content.Context
import com.piso12.indoorex.dtos.UserCampaignDto
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.services.campaign.callbacks.OnGetUserReactionsCallback
import com.piso12.indoorex.services.campaign.callbacks.OnPostReactionCallback
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.contracts.ICampaignReactionService
import com.piso12.indoorex.services.utils.ServiceCallback
import retrofit2.create

class CampaignReactionService(context: Context) {

    private val mContext = context

    fun postLikeReaction(campaignId: Long, userId: String,
                         callback: ServiceCallback<UserCampaignDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICampaignReactionService>()
            .postLikeReaction(campaignId, userId)
            .enqueue(OnPostReactionCallback(callback))
    }

    fun postDislikeReaction(campaignId: Long, userId: String,
                            callback: ServiceCallback<UserCampaignDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICampaignReactionService>()
            .postDislikeReaction(campaignId, userId)
            .enqueue(OnPostReactionCallback(callback))
    }

    fun getCampaignReactions(callback: ServiceCallback<List<UserCampaignDto>>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICampaignReactionService>()
            .getCampaignReactions()
            .enqueue(OnGetUserReactionsCallback(callback))
    }
}