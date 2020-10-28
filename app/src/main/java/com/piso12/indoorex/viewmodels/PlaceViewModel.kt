package com.piso12.indoorex.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piso12.indoorex.models.CurrentLocation
import com.piso12.indoorex.models.Place
import com.piso12.indoorex.models.Plan

class PlaceViewModel : ViewModel() {

    var mPlaces = MutableLiveData<List<Place>>()
    var mPlan = MutableLiveData<Plan>()
    var mCurrentLocation = MutableLiveData<CurrentLocation>()

    init {
        mPlaces.value = emptyList()
        mPlan.value = null
        mCurrentLocation.value = null
    }
}