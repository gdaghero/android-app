package com.piso12.indoorex.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.piso12.indoorex.models.Notification
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.firebase.RemoteConfigService
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.RetrofitClientLocations
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import java.time.LocalDateTime

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRetrofitClients()
        fetchRemoteConfig()
    }

    private fun initRetrofitClients() {
        RetrofitClientBackoffice.initClient(this)
        RetrofitClientLocations.initClient(this)
    }

    private fun fetchRemoteConfig() {
        RemoteConfigService(this).fetchRemoteConfig(
            object : ServiceCallback<String> {
                override fun onComplete(result: ServiceResult<String>) {
                    if (AuthService.isUserAuthenticated()) {
                        startActivity(createMainActivityIntent())
                    } else {
                        startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
                    }
                    finish()
                }
            }
        )
    }

    private fun createMainActivityIntent(): Intent {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        if (areExtrasValid()) {
            mainActivityIntent.putExtra("notification",
                Notification(
                    intent.extras["campaign_id"].toString().toLong(),
                    getDrawIdOrNull(),
                    intent.extras["title"].toString(),
                    intent.extras["body"].toString(),
                    intent.extras["image_url"].toString(),
                    LocalDateTime.now().toString(),
                    true
                )
            )
        }
        return mainActivityIntent
    }

    private fun getDrawIdOrNull(): Long? {
        return if (intent.hasExtra("draw_id")) {
            intent.extras["draw_id"].toString().toLong()
        } else {
            null
        }
    }

    private fun areExtrasValid(): Boolean {
        return intent.hasExtra("campaign_id") && intent.hasExtra("title")
                && intent.hasExtra("body") && intent.hasExtra("image_url")
    }
}
