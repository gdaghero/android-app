package com.piso12.indoorex.services.user.callbacks

import com.google.gson.JsonParser
import com.piso12.indoorex.exceptions.auth.SignUpBackofficeException
import com.piso12.indoorex.exceptions.auth.SignUpUserAlreadyExistsException
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnPostUserCallback(delegate: ServiceCallback<String>) : Callback<ResponseBody> {

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
                mDelegate.onComplete(ServiceResult("OK"))
            } else {
                response.errorBody()?.let {
                    val msg = JsonParser().parse(it.string()).asJsonObject
                    if (msg["message"].asString.contains("constraint")) {
                        throw SignUpUserAlreadyExistsException()
                    }
                }
                throw SignUpBackofficeException()
            }
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(ex))
        }
    }
}
