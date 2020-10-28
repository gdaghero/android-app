package com.piso12.indoorex.fragments.feed

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.fragments.campaign.CampaignListFragment
import com.piso12.indoorex.fragments.search.stores.StoreDetailMapFragment
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.interactors.NotificationInteractor
import com.piso12.indoorex.interactors.PlaceInteractor
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.models.Notification
import com.piso12.indoorex.services.location.BeaconService
import com.piso12.indoorex.services.location.LocationService
import com.piso12.indoorex.viewmodels.CampaignViewModel
import com.piso12.indoorex.viewmodels.FilterViewModel
import com.piso12.indoorex.viewmodels.NotificationViewModel
import com.piso12.indoorex.viewmodels.PlaceViewModel
import kotlinx.android.synthetic.main.feed_fragment.*


class FeedFragment : Fragment() {

    private lateinit var mNotificationViewModel: NotificationViewModel
    private lateinit var mCampaignViewModel: CampaignViewModel
    private lateinit var mFilterViewModel: FilterViewModel
    private lateinit var mPlaceViewModel: PlaceViewModel
    private lateinit var mCampaignInteractor: CampaignInteractor
    private lateinit var mLocationService: LocationService
    private lateinit var mLocationRunnable: Runnable
    private lateinit var mHandler: Handler
    private var mServiceConnection: ServiceConnection? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeInteractors()
        initializeObservers()
        handleNotification()
        handleNotifications()
        handleFilters()
        fetchNotifications()
        loadCampaigns()
    }

    private fun initializeInteractors() {
        mCampaignInteractor = CampaignInteractor(this)
        activity?.let {
            PlaceInteractor.initialize(it)
        }
    }

    private fun loadCampaigns() {
        mFilterViewModel.mFilter.value = FilterCampaign()
        val campaignsFragment = CampaignListFragment(this)
        fragmentManager?.let {
            it.beginTransaction()
                .add(R.id.feed_swipe_refresh, campaignsFragment)
                .commit()
        }
        feed_swipe_refresh.setOnRefreshListener {
            campaignsFragment.fetchCampaigns()
        }
    }

    private fun handleFilters() {
        ib_feed_filters.setOnClickListener {
            val filters = FeedFiltersFragment()
            val background = BackgroundFragment("#66000000")
            fragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                ?.add(R.id.feed_container, background, BackgroundFragment::class.simpleName)
                ?.addToBackStack(null)
                ?.setCustomAnimations(R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                    R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom)
                ?.add(R.id.feed_container, filters)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    private fun fetchNotifications() {
        NotificationInteractor.fetchNotifications()
    }

    private fun handleNotifications() {
        ib_notifications.setOnClickListener {
            NotificationInteractor.showNotifications()
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mFilterViewModel = ViewModelProviders.of(it).get(FilterViewModel::class.java)
            mNotificationViewModel = ViewModelProviders.of(it).get(NotificationViewModel::class.java)
            mCampaignViewModel = ViewModelProviders.of(it).get(CampaignViewModel::class.java)
            mPlaceViewModel = ViewModelProviders.of(it).get(PlaceViewModel::class.java)
        }
        observeNotifications()
        observeCampaigns()
        observeIncomingNotification()
    }

    private fun observeNotifications() {
        mNotificationViewModel.mNotifications.observe(viewLifecycleOwner, Observer { notifications ->
            updateNotificationsBadge(notifications.filter { !it.isRead }.size)
        })
        mPlaceViewModel.mCurrentLocation.observe(viewLifecycleOwner, Observer { location ->
            location?.let { location ->
                location_progress.visibility = View.GONE
                iv_location.visibility = View.VISIBLE
                if (location.placeId == 0) {
                    iv_location.setImageDrawable(
                        context?.getDrawable(R.drawable.ic_refresh_24px)
                            .also { it?.setTint(Color.BLACK) }
                    )
                    iv_location.setOnClickListener { getCurrentLocation() }
                } else {
                    tv_current_location.text = location.name
                    iv_location.setImageDrawable(
                        context?.getDrawable(R.drawable.ic_room_24px)
                            .also { it?.setTint(Color.parseColor("#1877F2")) }
                    )
                    iv_location.setOnClickListener { showCurrentLocation() }
                }
            }
        })
    }

    private fun getCurrentLocation() {
        iv_location.visibility = View.GONE
        location_progress.visibility = View.VISIBLE
        mLocationService.getLocationDto()?.let {
            PlaceInteractor.getCurrentLocation(it)
            mHandler.removeCallbacks(mLocationRunnable)
            mHandler.postDelayed(mLocationRunnable, 5 * 1000)
        }
    }

    private fun showCurrentLocation() {
        fragmentManager?.let { fm ->
            val currentLocation = mPlaceViewModel.mCurrentLocation.value!!
            val storeMap = StoreDetailMapFragment()
            storeMap.arguments = Bundle().also {
                it.putInt("planId", currentLocation.planId.toInt())
                it.putInt("x", currentLocation.x.toInt())
                it.putInt("y", currentLocation.y.toInt())
            }
            fm.beginTransaction()
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .add(R.id.feed_container, BackgroundFragment("#DD000000"))
                .setCustomAnimations(
                    R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                    R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom
                )
                .add(R.id.feed_container, storeMap)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeCampaigns() {
        mCampaignViewModel.mCampaigns.observe(viewLifecycleOwner, Observer {
            updateTitle()
            feed_swipe_refresh.isRefreshing = false
        })
    }

    private fun updateTitle() {
        mFilterViewModel.mFilter.value?.let {
            if (it.mShowDiscounts && !it.mShowEvents) {
                feed_title.text = "Explorar promociones"
            }
            if (!it.mShowDiscounts && it.mShowEvents) {
                feed_title.text = "Explorar eventos"
            }
            if (it.mShowDiscounts && it.mShowEvents) {
                feed_title.text = "Explorar"
            }
        }
    }

    private fun observeIncomingNotification() {
        mNotificationViewModel.mIncomingNotification.observe(viewLifecycleOwner, Observer { notification ->
            notification?.let {
                mCampaignInteractor.fetchCampaign(notification.campaignId)
                mNotificationViewModel.mIncomingNotification.postValue(null)
            }
        })
    }

    private fun handleNotification() {
        arguments?.let {
            mNotificationViewModel.mIncomingNotification.postValue(
                it.getSerializable("notification") as Notification
            )
        }
    }

    private fun updateNotificationsBadge(count: Int) {
        if (count > 0) {
            tv_notifications_count.visibility = View.VISIBLE
        } else {
            tv_notifications_count.visibility = View.INVISIBLE
        }
        tv_notifications_count.text = count.toString()
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            if (!ContextHelper.isServiceRunning(BeaconService::class.java, it)) {
                it.startService(Intent(it, BeaconService::class.java))
            }
            if (!ContextHelper.isServiceRunning(LocationService::class.java, it)) {
                it.startService(Intent(it, LocationService::class.java))
            }
            if (mServiceConnection == null) {
                initializeServiceConnection()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mCampaignInteractor.removeCallbacks()
        cancelLocationUpdates()
    }

    private fun cancelLocationUpdates() {
        activity?.let {
            if (::mHandler.isInitialized) {
                mHandler.removeCallbacks(mLocationRunnable)
            }
            if (::mLocationService.isInitialized && mServiceConnection != null) {
                it.unbindService(mServiceConnection)
                mServiceConnection = null
            }
        }
    }

    private fun initializeServiceConnection() {
        activity?.let {
            mServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, binder: IBinder) {
                    mLocationService = (binder as LocationService.LocationServiceBinder).getService()
                    requestLocations()
                }
                override fun onServiceDisconnected(arg0: ComponentName) {
                    activity?.unbindService(mServiceConnection)
                    mServiceConnection = null
                    mHandler.removeCallbacks(mLocationRunnable)
                }
            }
            it.bindService(
                Intent(it, LocationService::class.java),
                mServiceConnection,
                0
            )
        }
    }

    private fun requestLocations() {
        mHandler = Handler()
        mLocationRunnable = object : Runnable {
            override fun run() {
                mHandler.postDelayed(this, 1000)
                getCurrentLocation()
            }
        }
        mHandler.post(mLocationRunnable)
    }
}