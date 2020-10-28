package com.piso12.indoorex.services.place

import android.content.Context
import com.piso12.indoorex.dtos.CurrentLocationDto
import com.piso12.indoorex.dtos.LocationDto
import com.piso12.indoorex.dtos.PlanDto
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.models.Place
import com.piso12.indoorex.services.place.callbacks.OnGetCurrentLocationCallback
import com.piso12.indoorex.services.place.callbacks.OnGetPlacesCallback
import com.piso12.indoorex.services.place.callbacks.OnGetPlanCallback
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.RetrofitClientLocations
import com.piso12.indoorex.services.retrofit.contracts.IPlaceService
import com.piso12.indoorex.services.utils.ServiceCallback
import retrofit2.create

class PlaceService(context: Context) {

    private val mContext = context

    fun getPlaces(callback: ServiceCallback<List<Place>>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<IPlaceService>()
            .getPlaces().enqueue(OnGetPlacesCallback(callback))
    }

    fun getPlan(planId: Number, callback: ServiceCallback<PlanDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<IPlaceService>()
            .getPlan(planId).enqueue(OnGetPlanCallback(callback))
    }

    fun getCurrentLocation(location: LocationDto, callback: ServiceCallback<CurrentLocationDto>) {
        RetrofitClientLocations
            .get(ContextHelper.getLocationsApiUrl(mContext))
            .create<IPlaceService>()
            .getCurrentLocation(location).enqueue(OnGetCurrentLocationCallback(callback))
    }
}