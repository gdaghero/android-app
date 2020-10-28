package com.piso12.indoorex.services.retrofit

import android.content.Context
import com.piso12.indoorex.helpers.ContextHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientLocations {

    private lateinit var mRetrofit: Retrofit
    private lateinit var httpClient: OkHttpClient

    fun initClient(context: Context){
        if(!::httpClient.isInitialized) {
            httpClient = OkHttpClient.Builder()
                .authenticator(RefreshTokenAuthenticator(context))
                .addInterceptor { chain ->
                    val token = ContextHelper.getFirebaseJwtToken(context)
                    val newRequest = chain.request().newBuilder();
                    if (token.isNotEmpty()) {
                        newRequest.header("Authorization", "Bearer $token")
                    }
                    chain.proceed(newRequest.build()) }
                .connectTimeout(2, TimeUnit.SECONDS)
                .build()
        }
    }

    fun get(apiUrl: String): Retrofit {
        if (!::mRetrofit.isInitialized) {
            try {
                mRetrofit = Retrofit.Builder()
                    .baseUrl(apiUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            } catch (ex: Exception) {
                throw ex
            }
        }
        return mRetrofit
    }
}