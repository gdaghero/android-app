package com.piso12.indoorex.exceptions

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.piso12.indoorex.R
import java.io.IOException

abstract class ExceptionHandler(context: Context) : IExceptionHandler {

    private val mContext = context

    override fun showError(container: View, tag: String, error: Throwable) {
        var message = when (error) {
            is IOException -> mContext.getString(R.string.network_error)
            is IndoorexException -> mContext.getString(R.string.fetch_error)
            else -> mContext.getString(R.string.unexpected_error)
        }
        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
        logError(tag, error)
    }

    override fun logError(tag: String, error: Throwable?) {
        error?.let {
            error.printStackTrace()
            if (it.message == null) {
                Log.e(tag, it.toString())
            } else {
                Log.e(tag, it.message)
            }
        }
    }

}

interface IExceptionHandler {
    fun showError(container: View, tag: String, error: Throwable)
    fun logError(tag: String, error: Throwable?)
}
