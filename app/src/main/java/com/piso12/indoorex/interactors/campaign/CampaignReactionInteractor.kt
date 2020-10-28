package com.piso12.indoorex.interactors.campaign

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.dtos.UserCampaignDto
import com.piso12.indoorex.exceptions.campaign.CampaignExceptionHandler
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.campaign.CampaignReactionService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.CampaignViewModel
import kotlinx.android.synthetic.main.feed_fragment.*

class CampaignReactionInteractor(root: Fragment) {

    private val mActivity = root.activity!!
    private val mContainer = root.view!!

    private val mCampaignReactionService = CampaignReactionService(mActivity)
    private val mCampaignViewModel = ViewModelProviders.of(mActivity).get(CampaignViewModel::class.java)
    private var mIsFragmentAttached = true
    private val TAG = "CampaignReactionInteractor"

    fun onLike(campaign: CampaignDto) {
        mIsFragmentAttached = true
        mActivity.feed_swipe_refresh?.let { it.isRefreshing = true }
        mCampaignReactionService.postLikeReaction(
            campaign.id,
            AuthService.getCurrentUser().uid,
            onPostReactionResponse()
        )
    }

    fun onDislike(campaign: CampaignDto) {
        mIsFragmentAttached = true
        mActivity.feed_swipe_refresh?.let { it.isRefreshing = true }
        mCampaignReactionService.postDislikeReaction(
            campaign.id,
            AuthService.getCurrentUser().uid,
            onPostReactionResponse()
        )
    }

    private fun onPostReactionResponse(): ServiceCallback<UserCampaignDto> {
        return object : ServiceCallback<UserCampaignDto> {
            override fun onComplete(result: ServiceResult<UserCampaignDto>) {
                if (mIsFragmentAttached) {
                    mActivity.feed_swipe_refresh?.let { it.isRefreshing = false }
                    if (result.isSuccessful) {
                        val reaction = result.result!!
                        val campaigns = mCampaignViewModel.mCampaigns.value!!
                        val campaign = campaigns.find { it.id == reaction.campaignId }!!
                        campaign.isLiked = reaction.isLiked
                        campaign.isDisliked = reaction.isDisliked
                        campaign.isUsed = reaction.isUsed
                        campaigns.toMutableList().also {
                            val index = it.indexOf(campaign)
                            it.remove(campaign)
                            it.add(index, campaign)
                            mCampaignViewModel.mCampaigns.postValue(it)
                        }
                    } else {
                        CampaignExceptionHandler.getInstance(mActivity)
                            .showError(mContainer, TAG, result.error!!)
                    }
                }
            }
        }
    }

    fun fetchCampaignReactions(campaigns: List<CampaignDto>) {
        mIsFragmentAttached = true
        mActivity.feed_swipe_refresh?.let { it.isRefreshing = true }
        mCampaignReactionService.getCampaignReactions(onGetCampaignReactionsResponse(campaigns))
    }

    private fun onGetCampaignReactionsResponse(campaigns: List<CampaignDto>)
            : ServiceCallback<List<UserCampaignDto>> {
        return object : ServiceCallback<List<UserCampaignDto>> {
            override fun onComplete(result: ServiceResult<List<UserCampaignDto>>) {
                if (mIsFragmentAttached) {
                    mActivity.feed_swipe_refresh?.let { it.isRefreshing = false }
                    if (result.isSuccessful) {
                        mCampaignViewModel.mCampaigns.postValue(
                            joinCampaigns(campaigns, result.result!!)
                        )
                    } else {
                        CampaignExceptionHandler.getInstance(mActivity)
                            .showError(mContainer, TAG, result.error!!)
                        mCampaignViewModel.mCampaigns.postValue(campaigns)
                    }
                }
            }
        }
    }

    private fun joinCampaigns(campaigns: List<CampaignDto>, userCampaigns: List<UserCampaignDto>)
            : List<CampaignDto> {
        var result = mutableListOf<CampaignDto>()
        campaigns.forEach { campaign ->
            val userCampaign = userCampaigns.find { it.campaignId == campaign.id
                            && it.userId == AuthService.getCurrentUser().uid }
            if (userCampaign != null) {
                campaign.isUsed = userCampaign.isUsed
                campaign.isLiked = userCampaign.isLiked
                campaign.isDisliked = userCampaign.isDisliked
            }
            if (!campaign.isDisliked) {
                result.add(campaign)
            }
        }
        return result
    }

    fun removeCallbacks() {
        mIsFragmentAttached = false
    }
}