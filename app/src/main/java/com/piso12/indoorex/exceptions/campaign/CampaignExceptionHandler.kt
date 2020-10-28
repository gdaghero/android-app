package com.piso12.indoorex.exceptions.campaign

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.piso12.indoorex.R
import com.piso12.indoorex.exceptions.ExceptionHandler

abstract class CampaignExceptionHandler(context: Context) : ExceptionHandler(context) {

    private val mContext = context

    override fun showError(container: View, tag: String, error: Throwable) {
        val message = when (error) {
            is CreateCampaignQrCodeException ->
                mContext.getString(R.string.qr_code_generation_error)
            is FetchCampaignException ->
                mContext.getString(R.string.fetch_error)
            else -> ""
        }
        if (message == "") {
            super.showError(container, tag, error)
        } else {
            Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
        }
    }

    companion object {

        private lateinit var mInstance: ExceptionHandler

        fun getInstance(context: FragmentActivity): ExceptionHandler {
            if (!::mInstance.isInitialized) {
                mInstance = object : CampaignExceptionHandler(context) { }
            }
            return mInstance
        }
    }
}