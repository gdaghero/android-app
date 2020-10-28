package com.piso12.indoorex.services.retrofit.contracts

import com.piso12.indoorex.dtos.CategoryDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ICategoryService {

    @GET("categories")
    fun getCategories(): Call<ResponseBody>

    @POST("categories/users/{userId}")
    fun postCategories(@Path("userId") userId: String,
                       @Body categories: List<CategoryDto>): Call<ResponseBody>
}