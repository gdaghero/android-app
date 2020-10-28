package com.piso12.indoorex.interactors

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.dtos.PlaceDto
import com.piso12.indoorex.exceptions.campaign.CampaignExceptionHandler
import com.piso12.indoorex.models.SearchResult
import com.piso12.indoorex.services.search.SearchService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.FilterViewModel

class SearchInteractor(root: Fragment) {

    private val mActivity = root.activity!!
    private val mContainer = root.view!!

    private val mFilterViewModel = ViewModelProviders.of(mActivity).get(FilterViewModel::class.java)
    private val mSearchService = SearchService(mActivity)
    private var mIsFragmentAttached = true
    private val mTag = "SearchInteractor"

    fun search(text: String) {
        mIsFragmentAttached = true
        mSearchService.search(text, onSearchCompleted())
    }

    private fun onSearchCompleted(): ServiceCallback<List<Any>> {
        return object : ServiceCallback<List<Any>> {
            override fun onComplete(result: ServiceResult<List<Any>>) {
                if (mIsFragmentAttached) {
                    if (result.isSuccessful) {
                        val campaigns = result.result!!.filter { it.javaClass.simpleName == "CampaignDto" }
                                as List<CampaignDto>
                        val places = result.result!!.filter { it.javaClass.simpleName == "PlaceDto" }
                                as List<PlaceDto>
                        val searchResult = SearchResult().also { it.mCampaigns = campaigns }
                        searchResult.mStores = places.map { it.toModel() }
                        mFilterViewModel.mSearchResult.postValue(searchResult)
                    } else {
                        CampaignExceptionHandler.getInstance(mActivity).showError(
                            mContainer,
                            mTag,
                            result.error!!
                        )
                    }
                }
            }
        }
    }
}