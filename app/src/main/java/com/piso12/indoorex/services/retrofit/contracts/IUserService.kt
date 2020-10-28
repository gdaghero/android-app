package com.piso12.indoorex.services.retrofit.contracts

import com.piso12.indoorex.dtos.UserDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IUserService {

    @POST("users")
    fun postUser(@Body userDto: UserDto): Call<ResponseBody>

    @GET("users/{userId}")
    fun getUser(@Path("userId") userId: String): Call<ResponseBody>

    @POST("users/{userId}/edit")
    fun editUser(@Path("userId") userId: String, @Body userDto: UserDto): Call<ResponseBody>
}
