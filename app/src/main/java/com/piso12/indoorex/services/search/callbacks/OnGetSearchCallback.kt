package com.piso12.indoorex.services.search.callbacks

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.dtos.PlaceDto
import com.piso12.indoorex.dtos.SearchResultDto
import com.piso12.indoorex.exceptions.IndoorexException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnGetSearchCallback(callback: ServiceCallback<List<Any>>): Callback<ResponseBody> {

    private val mDelegate = callback

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            val bodyString = response.body()?.string()!!
            val placesDtos = JsonParser<SearchResultDto>()
                .parseAsList(bodyString, object : TypeToken<List<SearchResultDto>>(){}.type)
            val places = placesDtos.map { parsePlace(it) }
            mDelegate.onComplete(ServiceResult(places))
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(IndoorexException()))
        }
    }

    private fun parsePlace(it: SearchResultDto): Any {
        return when (it.objType) {
            "campaign" -> {
                JsonParser<CampaignDto>().parseAsObject(
                    Gson().toJsonTree(it.obj).toString(),
                    object : TypeToken<CampaignDto>(){}.type
                )
            }
            "place" -> {
                JsonParser<PlaceDto>().parseAsObject(
                    Gson().toJsonTree(it.obj).toString(),
                    object : TypeToken<PlaceDto>(){}.type
                )
            }
            else -> throw Exception()
        }
    }
}