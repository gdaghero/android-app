package com.piso12.indoorex.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piso12.indoorex.dtos.CampaignDto

class CampaignViewModel : ViewModel() {

    var mCampaigns = MutableLiveData<List<CampaignDto>>()

    init {
        mCampaigns.value = emptyList()
    }
}