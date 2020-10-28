package com.piso12.indoorex.services.location

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.altbeacon.beacon.*
import java.util.concurrent.locks.ReentrantReadWriteLock


class BeaconService : Service(), BeaconConsumer {

    private lateinit var mBeaconManager: BeaconManager
    private lateinit var mBinder: BeaconServiceBinder
    private lateinit var mRegion: Region
    private lateinit var mNearbyBeacons: HashMap<String, Double>
    private lateinit var mLock: ReentrantReadWriteLock
    private lateinit var mBroadcastReceiver: BroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        registerBroadcastReceiver()
    }

    private fun registerBroadcastReceiver() {
        mBroadcastReceiver = BeaconServiceBroadcastReceiver()
        val intentFilters = IntentFilter()
        intentFilters.addAction("STOP_SERVICE_EVENT")
        registerReceiver(mBroadcastReceiver, intentFilters)
    }

    override fun onBeaconServiceConnect() {
        mBeaconManager.removeAllRangeNotifiers()
        mBeaconManager.addRangeNotifier { beacons, _ ->
            updateNearbyBeacons(beacons)
        }
        mRegion = Region(FIRST_FLOOR, null, null, null)
        mBeaconManager.startRangingBeaconsInRegion(mRegion)
    }

    private fun updateNearbyBeacons(beacons: MutableCollection<Beacon>) {
        mLock.writeLock().lock()
        try {
            mNearbyBeacons.clear()
            Log.i(TAG, "Filtered beacons: ")
            beacons.forEach { beacon ->
                if (beacon.distance > 0 && beacon.rssi >= -75) {
                    Log.i(TAG, "${beacon.bluetoothAddress}: ${beacon.rssi}")
                    mNearbyBeacons["${beacon.id1}${beacon.id2}"] = beacon.distance
                }
            }
        } finally {
            mLock.writeLock().unlock()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mBinder = BeaconServiceBinder()
        mLock = ReentrantReadWriteLock()
        mBeaconManager = BeaconManager.getInstanceForApplication(this)
        mBeaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
        mBeaconManager.beaconParsers
            .add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT))
        mBeaconManager.bind(this)
        mNearbyBeacons = HashMap()
        Log.i(TAG, "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mBeaconManager.isInitialized) {
            mBeaconManager.removeAllRangeNotifiers()
            mBeaconManager.unbind(this)
        }
        if (::mBroadcastReceiver.isInitialized) {
            unregisterReceiver(mBroadcastReceiver)
        }
        if (::mNearbyBeacons.isInitialized) {
            mNearbyBeacons.clear()
        }
    }

    fun getNearbyBeacons(): HashMap<String, Double> {
        mLock.readLock().lock()
        try {
            return mNearbyBeacons
        } finally {
            mLock.readLock().unlock()
        }
    }

    inner class BeaconServiceBinder : Binder() {
        fun getService(): BeaconService = this@BeaconService
    }

    private inner class BeaconServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "STOP_SERVICE_EVENT" -> stopSelf()
            }
        }
    }

    private companion object {
        private const val TAG = "BeaconService"
        private const val FIRST_FLOOR = "P2"
    }
}
