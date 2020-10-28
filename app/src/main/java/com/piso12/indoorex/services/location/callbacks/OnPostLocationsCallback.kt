package com.piso12.indoorex.services.location.callbacks

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnPostLocationsCallback : Callback<ResponseBody> {

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        t.message?.let {
            Log.e(TAG, it)
        }
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            Log.i(TAG, response.message())
        } else {
            response.errorBody()?.let {
                Log.e(TAG, it.string())
            }
        }
    }

    private companion object {
        private const val TAG = "OnPostLocationsCallback"
    }

}