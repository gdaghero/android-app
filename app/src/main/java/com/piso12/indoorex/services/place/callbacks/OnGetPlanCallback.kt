package com.piso12.indoorex.services.place.callbacks

import com.google.common.reflect.TypeToken
import com.piso12.indoorex.dtos.PlanDto
import com.piso12.indoorex.exceptions.IndoorexException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnGetPlanCallback(delegate: ServiceCallback<PlanDto>): Callback<ResponseBody> {

    private val mDelegate = delegate

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            val bodyString = response.body()?.string()!!
            val planDto = JsonParser<PlanDto>()
                .parseAsObject(bodyString, object : TypeToken<PlanDto>(){}.type)
            mDelegate.onComplete(ServiceResult(planDto))
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(IndoorexException()))
        }
    }
}