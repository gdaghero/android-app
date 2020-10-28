package com.piso12.indoorex.fragments.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.fragments.feed.BackgroundFragment
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import kotlinx.android.synthetic.main.feed_item_options.*


class CampaignOptionsFragment : Fragment() {

    private lateinit var mBottomSheet: BottomSheetBehavior<RelativeLayout>
    private lateinit var mCampaignInteractor: CampaignInteractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_item_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initInteractors()
        handleBottomSheetPanel()
        handleDislike()
    }

    private fun initInteractors() {
        mCampaignInteractor = CampaignInteractor(this)
    }

    private fun handleDislike() {
        val campaign = arguments?.getSerializable("campaign") as CampaignDto
        btn_dislike_campaign.setOnClickListener {
            mCampaignInteractor.onCampaignClickListener().onDislike(campaign)
            removeFragments()
        }
    }

    private fun handleBottomSheetPanel() {
        mBottomSheet = BottomSheetBehavior.from(bs_feed_item_options as RelativeLayout)
        mBottomSheet.skipCollapsed = true
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        val shadowFragment = fragmentManager?.findFragmentByTag(BackgroundFragment::class.simpleName)
                as BackgroundFragment
        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, offsetPx: Float) {
                shadowFragment.onClosing(offsetPx)
            }
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    removeFragments()
                }
            }
        })
    }

    private fun removeFragments() {
        fragmentManager?.let {
            it.popBackStack()
            it.popBackStack()
        }
    }
}