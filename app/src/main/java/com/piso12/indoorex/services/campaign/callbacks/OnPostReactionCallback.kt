package com.piso12.indoorex.services.campaign.callbacks

import com.google.common.reflect.TypeToken
import com.piso12.indoorex.dtos.UserCampaignDto
import com.piso12.indoorex.exceptions.campaign.PostReactionException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnPostReactionCallback(delegate: ServiceCallback<UserCampaignDto>) : Callback<ResponseBody> {

    private val mDelegate = delegate

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            if (response.isSuccessful) {
                handleSuccess(response.body())
            } else {
                throw Exception()
            }
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(PostReactionException()))
        }
    }

    private fun handleSuccess(body: ResponseBody?) {
        body?.let {
            val userCampaigns = JsonParser<UserCampaignDto>()
                .parseAsObject(it.string(), object : TypeToken<UserCampaignDto>(){}.type)
            mDelegate.onComplete(ServiceResult(userCampaigns))
        }
    }
}