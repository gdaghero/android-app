package com.piso12.indoorex.services.notification.callbacks

import com.google.common.reflect.TypeToken
import com.piso12.indoorex.dtos.UserCampaignDto
import com.piso12.indoorex.exceptions.IndoorexException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnPostNotificationOpenedCallback(delegate: ServiceCallback<UserCampaignDto>) : Callback<ResponseBody> {

    private val mDelegate = delegate

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val bodyString = body.string()
                    val userCampaignDto = JsonParser<UserCampaignDto>()
                        .parseAsObject(bodyString, object : TypeToken<UserCampaignDto>(){}.type)
                    mDelegate.onComplete(ServiceResult(userCampaignDto))
                }
            } else {
                throw IndoorexException()
            }
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(IndoorexException()))
        }
    }
}