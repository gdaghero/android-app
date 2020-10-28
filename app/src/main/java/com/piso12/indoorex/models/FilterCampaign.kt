package com.piso12.indoorex.models

import com.piso12.indoorex.dtos.CampaignDto
import java.io.Serializable

class FilterCampaign : Serializable {

    var mShowDiscounts = true
    var mShowEvents = true
    var mShowBookmarks = false
    var mCategory: Category ?= null
    var mStore: Place ?= null
    var mCampaigns: List<CampaignDto> ?= null
}