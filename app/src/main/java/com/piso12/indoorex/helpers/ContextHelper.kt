package com.piso12.indoorex.helpers

import android.app.ActivityManager
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import com.google.gson.JsonParser
import com.piso12.indoorex.BuildConfig
import com.piso12.indoorex.R
import java.text.DateFormatSymbols
import java.util.*


object ContextHelper {

    private fun getUrl(context: Context, key: String): String {
        val json = JsonParser().parse(getProperty(context, key)).asJsonObject
        var apiUrl = json["url"].asString
        val port = json["port"]
        apiUrl = if (port != null) {
            "$apiUrl:${port.asString}/"
        } else {
            "$apiUrl/"
        }
        json["api"]?.let { api ->
            apiUrl = "$apiUrl${api.asString}/"
        }
        return apiUrl
    }

    fun getProperty(context: Context, key: String): String {
        val prefs
                = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        return prefs.getString(key, "")!!
    }

    fun setProperty(context: Context, key: String, value: String?) {
        val prefs
                = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        with (prefs.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun hideSoftKeyboard(activity: FragmentActivity, view: View) {
        val inputManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getBackofficeApiUrl(context: Context): String {
        return getUrl(context, context.getString(R.string.backoffice_api_url_key))
    }

    fun getLocationsApiUrl(context: Context): String {
        return getUrl(context, context.getString(R.string.locations_api_url_key))
    }

    fun getPostLocationsInterval(context: Context): Long {
        val value
                = getProperty(context, context.getString(R.string.location_post_interval_key))
        val json = JsonParser().parse(value).asJsonObject
        return if (json["value"] != null) {
            json["value"].asLong
        } else {
            2000
        }
    }

    fun getFirebaseToken(context: Context): String {
        val key = context.getString(R.string.firebase_auth_token_key)
        return getProperty(context, key)
    }

    fun getDayName(dayOfWeek: Int): String {
        val day = DateFormatSymbols(Locale("es", "ES")).weekdays[dayOfWeek]
        return day.substring(0, 1).toUpperCase() + day.substring(1)
    }

    fun getMonthName(monthNumber: Int): String {
        val month = DateFormatSymbols(Locale("es", "ES")).months[monthNumber]
        return month.substring(0, 1).toUpperCase() + month.substring(1)
    }

    fun getFirebaseJwtToken(context: Context): String {
        val key = context.getString(R.string.firebase_auth_jwt_token_key)
        return getProperty(context, key)
    }

    fun setFirebaseJwtToken(context: Context, value: String?) {
        val key = context.getString(R.string.firebase_auth_jwt_token_key)
        setProperty(context, key, value)
    }

    fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
