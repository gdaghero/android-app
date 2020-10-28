package com.piso12.indoorex.services.retrofit.contracts

import com.piso12.indoorex.dtos.LocationDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IPlaceService {

    @GET("places")
    fun getPlaces(): Call<ResponseBody>

    @GET("plans/{planId}")
    fun getPlan(@Path("planId") planId: Number): Call<ResponseBody>

    @POST("position")
    fun getCurrentLocation(@Body location: LocationDto): Call<ResponseBody>
}