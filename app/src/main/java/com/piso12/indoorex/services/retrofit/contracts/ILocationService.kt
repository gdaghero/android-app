package com.piso12.indoorex.services.retrofit.contracts

import com.piso12.indoorex.dtos.LocationDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ILocationService {

    @POST("locations")
    fun postNearbyBeacons(@Body distances: LocationDto): Call<ResponseBody>
}
