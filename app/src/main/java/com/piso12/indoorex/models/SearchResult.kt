package com.piso12.indoorex.models

import com.piso12.indoorex.dtos.CampaignDto

class SearchResult {

    var mCampaigns: List<CampaignDto> = emptyList()
    var mStores: List<Place> = emptyList()
}