package com.piso12.indoorex.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.piso12.indoorex.R
import com.piso12.indoorex.interactors.NotificationInteractor
import com.piso12.indoorex.interactors.UserInteractor
import com.piso12.indoorex.models.Notification
import com.piso12.indoorex.services.location.BeaconService
import com.piso12.indoorex.services.location.LocationService
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.RetrofitClientLocations
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initializeInteractors()
        setupFragmentNavigation()
        fetchUser()
        startServices()
    }

    private fun initializeInteractors() {
        NotificationInteractor.initialize(this)
        UserInteractor.initialize(this, container)
    }

    private fun fetchUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        UserInteractor.fetchUser(userId)
    }

    private fun setupFragmentNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)
        handleExtras()
    }

    private fun handleExtras() {
        if (intent.hasExtra("notification")) {
            val notification = intent.getSerializableExtra("notification") as Notification
            NotificationInteractor.addNotification(notification)
            NotificationInteractor.openNotification(notification)
        }
    }

    private fun startServices() {
        if (isLocationPermissionGranted()) {
            startService(Intent(this, BeaconService::class.java))
            startService(Intent(this, LocationService::class.java))
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        when (requestCode) {
            ACCESS_LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    != PackageManager.PERMISSION_GRANTED) {
                    val message = getString(R.string.msg_permissions_needed)
                    Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
                }
                startServices()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationInteractor.unregisterBroadcastReceiver()
    }

    private companion object {
        private const val ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
