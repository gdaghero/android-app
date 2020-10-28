package com.piso12.indoorex.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.models.SearchResult

class FilterViewModel : ViewModel() {

    var mFilter = MutableLiveData<FilterCampaign>()
    var mSearchResult = MutableLiveData<SearchResult>()

    init {
        mFilter.value = null
        mSearchResult.value = null
    }
}