package com.piso12.indoorex.services.campaign.callbacks

import com.google.common.reflect.TypeToken
import com.piso12.indoorex.dtos.DrawDto
import com.piso12.indoorex.exceptions.campaign.FetchCampaignException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnGetDrawCallback(delegate: ServiceCallback<DrawDto>): Callback<ResponseBody> {

    private val mDelegate = delegate

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            val bodyString = response.body()?.string()!!
            val draw = JsonParser<DrawDto>()
                .parseAsObject(bodyString, object : TypeToken<DrawDto>(){}.type)
            mDelegate.onComplete(ServiceResult(draw))
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(FetchCampaignException()))
        }
    }
}
