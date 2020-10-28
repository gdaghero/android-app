package com.piso12.indoorex.interactors

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.dtos.CurrentLocationDto
import com.piso12.indoorex.dtos.LocationDto
import com.piso12.indoorex.dtos.PlanDto
import com.piso12.indoorex.models.CurrentLocation
import com.piso12.indoorex.models.Place
import com.piso12.indoorex.services.place.PlaceService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.PlaceViewModel

object PlaceInteractor {

    private lateinit var mPlaceViewModel: PlaceViewModel
    private lateinit var mPlaceService: PlaceService
    private var mIsFragmentAttached = true

    fun getPlaces() {
        mIsFragmentAttached = true
        mPlaceService.getPlaces(onGetPlacesResponse())
    }

    fun getPlan(planId: Number) {
        mIsFragmentAttached = true
        mPlaceService.getPlan(planId, onGetPlanResponse())
    }

    fun getCurrentLocation(location: LocationDto) {
        mIsFragmentAttached = true
        mPlaceService.getCurrentLocation(location, onGetCurrentLocationResponse())
    }

    private fun onGetCurrentLocationResponse(): ServiceCallback<CurrentLocationDto> {
        return object : ServiceCallback<CurrentLocationDto> {
            override fun onComplete(result: ServiceResult<CurrentLocationDto>) {
                if (mIsFragmentAttached) {
                    if (result.isSuccessful) {
                        mPlaceViewModel.mCurrentLocation.postValue(result.result!!.toModel())
                    } else {
                        mPlaceViewModel.mCurrentLocation.postValue(
                            CurrentLocation(0, "Detectar ubicación", 0, 0, 0)
                        )
                    }
                }
            }
        }
    }

    private fun onGetPlanResponse(): ServiceCallback<PlanDto> {
        return object : ServiceCallback<PlanDto> {
            override fun onComplete(result: ServiceResult<PlanDto>) {
                if (mIsFragmentAttached) {
                    if (result.isSuccessful) {
                        mPlaceViewModel.mPlan.postValue(result.result!!.toModel())
                    }
                }
            }
        }
    }

    private fun onGetPlacesResponse(): ServiceCallback<List<Place>> {
        return object : ServiceCallback<List<Place>> {
            override fun onComplete(result: ServiceResult<List<Place>>) {
                if (mIsFragmentAttached) {
                    if (result.isSuccessful) {
                        mPlaceViewModel.mPlaces.postValue(result.result)
                    }
                }
            }
        }
    }

    fun initialize(context: FragmentActivity) {
        mPlaceViewModel = ViewModelProviders.of(context).get(PlaceViewModel::class.java)
        mPlaceService = PlaceService(context)
    }

    fun removeCallbacks() {
        mIsFragmentAttached = false
    }
}