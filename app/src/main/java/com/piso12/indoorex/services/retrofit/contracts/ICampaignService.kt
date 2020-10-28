package com.piso12.indoorex.services.retrofit.contracts

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ICampaignService {

    @GET("campaigns")
    fun getCampaigns(): Call<ResponseBody>

    @GET("campaigns/{campaignId}")
    fun getCampaign(@Path("campaignId") campaignId: Long): Call<ResponseBody>

    @POST("draws/{drawId}/user/{userId}")
    fun postDraw(@Path("drawId") drawId: Number, @Path("userId") userId: String): Call<ResponseBody>

    @GET("campaigns/{campaignId}/draw")
    fun getDraw(@Path("campaignId") campaignId: Long): Call<ResponseBody>
}
