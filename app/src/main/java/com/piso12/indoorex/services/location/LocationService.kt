package com.piso12.indoorex.services.location

import android.app.Service
import android.content.*
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.piso12.indoorex.dtos.LocationDto
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.location.callbacks.OnPostLocationsCallback
import com.piso12.indoorex.services.retrofit.RetrofitClientLocations
import com.piso12.indoorex.services.retrofit.contracts.ILocationService
import retrofit2.create
import java.time.LocalDateTime

class LocationService : Service() {

    private var mServiceConnection: ServiceConnection ?= null
    private var mBroadcastReceiver: BroadcastReceiver ?= null
    private lateinit var mFirebaseToken: String
    private lateinit var mUid: String
    private lateinit var mLocationsApiUrl: String
    private lateinit var mBeaconService: BeaconService
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var mBinder: LocationServiceBinder

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        registerBroadcastReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindBeaconService()
        setInterval()
        setLocationsApiUrl()
        setUserInformation()
        Log.i(TAG, "Service started")
        mBinder = LocationServiceBinder()
        return START_STICKY
    }

    private fun bindBeaconService() {
        mServiceConnection = getServiceConnection()
        bindService(
            Intent(this, BeaconService::class.java),
            mServiceConnection,
            0
        )
    }

    private fun getServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                mBeaconService = (binder as BeaconService.BeaconServiceBinder).getService()
                startSendingLocations()
            }
            override fun onServiceDisconnected(arg0: ComponentName) {
                unbindService(mServiceConnection)
                unregisterReceiver(mBroadcastReceiver)
                mServiceConnection = null
                mBroadcastReceiver = null
                stopSendingLocations()
            }
        }
    }

    private fun setUserInformation() {
        mUid = AuthService.getCurrentUser().uid
        mFirebaseToken = ContextHelper.getFirebaseToken(this)
    }

    private fun setLocationsApiUrl() {
        mLocationsApiUrl = ContextHelper.getLocationsApiUrl(this)
        Log.i(TAG, "Sending locations to: $mLocationsApiUrl")
    }

    private fun startSendingLocations() {
        mHandler = Handler()
        mRunnable = object : Runnable {
            override fun run() {
                mHandler.postDelayed(this, mInterval)
                val beacons = mBeaconService.getNearbyBeacons()
                if (beacons.isNotEmpty()) {
                    sendDistances(LocationDto(mUid, mFirebaseToken, LocalDateTime.now().toString(), beacons, false))
                }
            }
        }
        mHandler.post(mRunnable)
    }

    private fun stopSendingLocations() {
        if (::mHandler.isInitialized) {
            mHandler.removeCallbacks(mRunnable)
        }
    }

    private fun sendDistances(beacons: LocationDto) {
        Log.i(TAG, "Sending data... ${beacons.distances.size} beacons nearby")
        Log.i(TAG, beacons.distances.toString())
        RetrofitClientLocations
            .get(mLocationsApiUrl)
            .create<ILocationService>()
            .postNearbyBeacons(beacons)
            .enqueue(OnPostLocationsCallback())
    }

    private fun registerBroadcastReceiver() {
        mBroadcastReceiver = LocationServiceBroadcastReceiver()
        val intentFilters = IntentFilter()
        intentFilters.addAction("CONFIG_CHANGE_EVENT")
        intentFilters.addAction("STOP_SERVICE_EVENT")
        registerReceiver(mBroadcastReceiver, intentFilters)
    }

    private fun setInterval() {
        mInterval = ContextHelper.getPostLocationsInterval(this)
        Log.i(TAG, "Sending locations in: ${mInterval}ms")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mServiceConnection != null) {
            unbindService(mServiceConnection)
            mServiceConnection = null
        }
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver)
            mBroadcastReceiver = null
        }
        stopSendingLocations()
    }

    private inner class LocationServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "CONFIG_CHANGE_EVENT" -> handleConfigChange()
                "STOP_SERVICE_EVENT" -> stopSelf()
            }
        }

        private fun handleConfigChange() {
            setInterval()
            setLocationsApiUrl()
            stopSendingLocations()
            startSendingLocations()
        }
    }

    private companion object {
        private const val TAG = "LocationService"
        private var mInterval = 2 * 1000L
    }

    fun getLocationDto(): LocationDto? {
        if (::mBeaconService.isInitialized) {
            val beacons = mBeaconService.getNearbyBeacons()
            if (beacons.isNotEmpty()) {
                return LocationDto(
                    mUid,
                    mFirebaseToken,
                    LocalDateTime.now().toString(),
                    beacons,
                    true)
            }
        }
        return null
    }

    inner class LocationServiceBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }
}


