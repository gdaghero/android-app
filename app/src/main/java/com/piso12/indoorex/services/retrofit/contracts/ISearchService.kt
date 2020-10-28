package com.piso12.indoorex.services.retrofit.contracts

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ISearchService {


    @GET("search")
    fun getSearch(@Query("text") searchText: String): Call<ResponseBody>
}