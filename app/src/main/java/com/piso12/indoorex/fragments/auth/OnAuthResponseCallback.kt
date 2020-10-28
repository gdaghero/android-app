package com.piso12.indoorex.fragments.auth

import android.content.Intent

interface OnAuthResponseCallback {
    fun onResponseRegister(intent: Intent)
    fun onResponseLogin(intent: Intent, jwt: String?, expire: Long)
    fun onCancel()
    fun onError(error: Exception)
}
