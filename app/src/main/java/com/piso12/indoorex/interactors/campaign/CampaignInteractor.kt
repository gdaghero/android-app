package com.piso12.indoorex.interactors.campaign

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.dtos.DrawDto
import com.piso12.indoorex.exceptions.campaign.CampaignExceptionHandler
import com.piso12.indoorex.fragments.campaign.DiscountDetailFragment
import com.piso12.indoorex.fragments.campaign.EventDetailFragment
import com.piso12.indoorex.fragments.campaign.EventDrawFragment
import com.piso12.indoorex.fragments.campaign.CampaignOptionsFragment
import com.piso12.indoorex.fragments.feed.BackgroundFragment
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.campaign.CampaignService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.CampaignViewModel
import com.piso12.indoorex.viewmodels.DrawViewModel
import kotlinx.android.synthetic.main.feed_fragment.*

class CampaignInteractor(root: Fragment) {

    private val mFragment = root
    private val mActivity = root.activity!!
    private val mContainer = root.view!!

    private val mCampaignViewModel = ViewModelProviders.of(mActivity).get(CampaignViewModel::class.java)
    private val mDrawViewModel = ViewModelProviders.of(mActivity).get(DrawViewModel::class.java)
    private val mCampaignService = CampaignService(mActivity)
    private var mIsFragmentAttached = true
    private val mCampaignReactionInteractor = CampaignReactionInteractor(root)
    private val mTag = "CampaignInteractor"

    fun fetchCampaign(id: Long) {
        mIsFragmentAttached = true
        mCampaignService.getCampaignById(id, onCampaignFetchResponse())
    }

    fun fetchCampaigns() {
        mIsFragmentAttached = true
        mActivity.feed_swipe_refresh?.let { it.isRefreshing = true }
        mCampaignService.getCampaigns(onCompleteFetchCampaigns())
    }

    fun fetchDraw(campaignId: Long) {
        mIsFragmentAttached = true
        mCampaignService.getDraw(
            campaignId,
            onCompleteGetDraw()
        )
    }

    private fun onCompleteGetDraw(): ServiceCallback<DrawDto> {
        return object : ServiceCallback<DrawDto> {
            override fun onComplete(result: ServiceResult<DrawDto>) {
                if (mIsFragmentAttached) {
                    if (result.isSuccessful) {
                        mDrawViewModel.mDraw.postValue(result.result!!.toModel())
                    } else {
                        CampaignExceptionHandler.getInstance(mActivity)
                            .showError(mContainer, mTag, result.error!!)
                    }
                }
            }
        }
    }

    private fun onCampaignFetchResponse(): ServiceCallback<CampaignDto> {
        return object : ServiceCallback<CampaignDto> {
            override fun onComplete(result: ServiceResult<CampaignDto>) {
                if (mIsFragmentAttached) {
                    if (result.isSuccessful) {
                        val campaign = result.result!!
                        if (campaign.eventEnabled) {
                            showEventDetail(campaign)
                        } else {
                            showDiscountDetail(campaign)
                        }
                    } else {
                        CampaignExceptionHandler.getInstance(mActivity)
                            .showError(mContainer, mTag, result.error!!)
                    }
                }
            }
        }
    }

    private fun onCompleteFetchCampaigns(): ServiceCallback<List<CampaignDto>> {
        return object :
            ServiceCallback<List<CampaignDto>> {
            override fun onComplete(result: ServiceResult<List<CampaignDto>>) {
                if (mIsFragmentAttached) {
                    mActivity.feed_swipe_refresh?.let { it.isRefreshing = false }
                    if (result.isSuccessful) {
                        mCampaignReactionInteractor.fetchCampaignReactions(result.result!!)
                    } else {
                        CampaignExceptionHandler.getInstance(mActivity)
                            .showError(mContainer, mTag, result.error!!)
                    }
                }
            }
        }
    }

    fun postDraw(drawId: Number, callback: EventDrawFragment.OnPostDrawCompletedListener) {
        mIsFragmentAttached = true
        mCampaignService.postDraw(
            drawId,
            AuthService.getCurrentUser().uid,
            onCompletePostDraw(callback)
        )
    }

    private fun onCompletePostDraw(callback: EventDrawFragment.OnPostDrawCompletedListener)
            : ServiceCallback<DrawDto> {
        return object : ServiceCallback<DrawDto> {
            override fun onComplete(result: ServiceResult<DrawDto>) {
                if (mIsFragmentAttached) {
                    if (result.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError()
                        CampaignExceptionHandler.getInstance(mActivity)
                            .showError(mContainer, mTag, result.error!!)
                    }
                }
            }
        }
    }

    fun onCampaignClickListener(): OnCampaignClickListener {
        return object : OnCampaignClickListener {
            override fun onClick(campaign: CampaignDto) {

                if (campaign.eventEnabled) {
                    showEventDetail(campaign)
                } else {
                    showDiscountDetail(campaign)
                }
            }
            override fun onBookmark(campaign: CampaignDto) {
                mCampaignService.bookmark(campaign.id.toInt())
                mCampaignViewModel.mCampaigns.postValue(mCampaignViewModel.mCampaigns.value)
            }

            override fun onLike(campaign: CampaignDto) {
                return mCampaignReactionInteractor.onLike(campaign)
            }

            override fun onDislike(campaign: CampaignDto) {
                return mCampaignReactionInteractor.onDislike(campaign)
            }

            override fun onOptionsClick(campaign: CampaignDto) {
                showCampaignOptions(campaign)
            }

            override fun isBookmarked(campaign: CampaignDto): Boolean {
                return mCampaignService.isBookmarked(campaign.id.toInt())
            }
        }
    }

    private fun showEventDetail(campaign: CampaignDto) {
        val eventDetailFragment = EventDetailFragment()
        eventDetailFragment.arguments = Bundle().also { it.putSerializable("campaign", campaign) }
        showFragment(eventDetailFragment)
    }

    private fun showDiscountDetail(campaign: CampaignDto) {
        val campaignDetailFragment = DiscountDetailFragment()
        campaignDetailFragment.arguments = Bundle().also { it.putSerializable("campaign", campaign) }
        showFragment(campaignDetailFragment)
    }

    private fun showCampaignOptions(campaign: CampaignDto) {
        val optionsFragment =
            CampaignOptionsFragment()
        optionsFragment.arguments = Bundle().also { it.putSerializable("campaign", campaign) }
        showFragment(optionsFragment)
    }

    private fun showFragment(fragment: Fragment) {
        mFragment.fragmentManager?.let {
            it.beginTransaction()
                .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                .add(mContainer.id, BackgroundFragment("#66000000"), BackgroundFragment::class.simpleName)
                .setCustomAnimations(R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                    R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom)
                .add(mContainer.id, fragment, fragment::class.simpleName)
                .addToBackStack(null)
                .commit()
        }
    }

    fun removeCallbacks() {
        mIsFragmentAttached = false
        mCampaignReactionInteractor.removeCallbacks()
    }

    fun isBookmarked(campaignId: Long): Boolean {
        return mCampaignService.isBookmarked(campaignId.toInt())
    }

    interface OnCampaignClickListener {
        fun onClick(campaign: CampaignDto)
        fun onBookmark(campaign: CampaignDto)
        fun onLike(campaign: CampaignDto)
        fun onDislike(campaign: CampaignDto)
        fun onOptionsClick(campaign: CampaignDto)
        fun isBookmarked(campaign: CampaignDto): Boolean
    }
}