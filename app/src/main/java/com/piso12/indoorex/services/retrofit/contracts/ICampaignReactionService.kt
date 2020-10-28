package com.piso12.indoorex.services.retrofit.contracts

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ICampaignReactionService {

    @POST("users-campaigns/likes")
    fun postLikeReaction(
        @Query("campaignId") campaignId: Long,
        @Query("userId") userId: String): Call<ResponseBody>

    @POST("users-campaigns/dislikes")
    fun postDislikeReaction(
        @Query("campaignId") campaignId: Long,
        @Query("userId") userId: String): Call<ResponseBody>

    @GET("users-campaigns")
    fun getCampaignReactions(): Call<ResponseBody>
}
