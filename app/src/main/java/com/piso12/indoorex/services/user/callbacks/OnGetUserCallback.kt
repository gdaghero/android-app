package com.piso12.indoorex.services.user.callbacks

import com.google.common.reflect.TypeToken
import com.piso12.indoorex.dtos.UserDto
import com.piso12.indoorex.exceptions.IndoorexException
import com.piso12.indoorex.exceptions.auth.UserNotFoundException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnGetUserCallback(delegate: ServiceCallback<UserDto>) : Callback<ResponseBody> {

    private val mDelegate = delegate

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        handlePostUserSuccess(response)
    }

    private fun handlePostUserSuccess(response: Response<ResponseBody>) {
        try {
            if (response.isSuccessful) {
                val bodyString = response.body()?.string()!!
                val user = JsonParser<UserDto>()
                    .parseAsObject(bodyString, object : TypeToken<UserDto>(){}.type)
                mDelegate.onComplete(ServiceResult(user))
            } else {
                if (response.code() == 404) {
                    throw UserNotFoundException()
                }
                throw IndoorexException()
            }
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(ex))
        }
    }
}
