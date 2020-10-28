package com.piso12.indoorex.services.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult

class RemoteConfigService(context: Context) {

    private val mContext = context

    fun fetchRemoteConfig(callback: ServiceCallback<String>) {
        val instance = FirebaseRemoteConfig.getInstance()
        instance.fetch(0)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    instance.activate().addOnCompleteListener {
                        handleRemoteConfigResult(instance.all)
                        callback.onComplete(ServiceResult("OK"))
                    }
                } else {
                    callback.onComplete(ServiceResult(task.exception!!))
                }
            }
    }

    fun fetchRemoteConfig() {
        val instance = FirebaseRemoteConfig.getInstance()
        instance.fetch(0)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    instance.activate().addOnCompleteListener {
                        handleRemoteConfigResult(instance.all)
                        mContext.sendBroadcast(Intent("CONFIG_CHANGE_EVENT"))
                    }
                }
            }
    }

    private fun handleRemoteConfigResult(config: Map<String, FirebaseRemoteConfigValue>) {
        config.map {
            Log.i(TAG, "${it.key}: ${it.value.asString()}")
            ContextHelper.setProperty(mContext, it.key, it.value.asString())
        }
    }

    private companion object {
        private const val TAG = "REMOTE_CONFIG_SERVICE"
    }
}
