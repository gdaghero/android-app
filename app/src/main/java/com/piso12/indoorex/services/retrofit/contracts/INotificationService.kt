package com.piso12.indoorex.services.retrofit.contracts

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface INotificationService {

    @POST("users-campaigns/opened")
    fun postOpenedNotification(
        @Query("campaignId") campaignId: Long,
        @Query("userId") userId: String): Call<ResponseBody>
}