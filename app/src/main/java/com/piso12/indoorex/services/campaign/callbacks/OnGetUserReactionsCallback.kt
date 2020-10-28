package com.piso12.indoorex.services.campaign.callbacks

import com.google.common.reflect.TypeToken
import com.google.gson.JsonParseException
import com.piso12.indoorex.dtos.UserCampaignDto
import com.piso12.indoorex.exceptions.campaign.FetchCampaignReactionsException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnGetUserReactionsCallback(delegate: ServiceCallback<List<UserCampaignDto>>):
    Callback<ResponseBody> {

    private val mDelegate = delegate

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            val bodyString = response.body()?.string()!!
            val userCampaigns = JsonParser<UserCampaignDto>()
                .parseAsList(bodyString, object : TypeToken<List<UserCampaignDto>>() {}.type)
            mDelegate.onComplete(ServiceResult(userCampaigns))
        } catch (ex: IllegalStateException) {
            mDelegate.onComplete(ServiceResult(FetchCampaignReactionsException()))
        } catch (ex: JsonParseException) {
            mDelegate.onComplete(ServiceResult(FetchCampaignReactionsException()))
        } catch (ex: KotlinNullPointerException) {
            mDelegate.onComplete(ServiceResult(FetchCampaignReactionsException()))
        }
    }
}
