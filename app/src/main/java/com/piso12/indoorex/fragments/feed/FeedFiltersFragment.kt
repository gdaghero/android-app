package com.piso12.indoorex.fragments.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.piso12.indoorex.R
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.viewmodels.FilterViewModel
import kotlinx.android.synthetic.main.feed_filters_fragment.*

class FeedFiltersFragment : Fragment() {

    private lateinit var mBottomSheet: BottomSheetBehavior<RelativeLayout>
    private lateinit var mFilterViewModel: FilterViewModel
    private lateinit var mCampaignInteractor: CampaignInteractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_filters_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeInteractors()
        initializeObservers()
        handleBottomSheetPanel()
        handleFilters()
        loadFilters()
    }

    private fun initializeInteractors() {
        mCampaignInteractor = CampaignInteractor(this)
    }

    private fun handleFilters() {
        iv_promotions.setOnClickListener {
            val filter = mFilterViewModel.mFilter.value
            if (filter == null) {
                val newFilter = FilterCampaign()
                newFilter.mShowEvents = true
                newFilter.mShowDiscounts = false
                mFilterViewModel.mFilter.postValue(newFilter)
            } else {
                if (filter.mShowEvents) {
                    filter.mShowDiscounts = !filter.mShowDiscounts
                    mFilterViewModel.mFilter.postValue(filter)
                }
            }
        }
        iv_events.setOnClickListener {
            val filter = mFilterViewModel.mFilter.value
            if (filter == null) {
                val newFilter = FilterCampaign()
                newFilter.mShowEvents = false
                newFilter.mShowDiscounts = true
                mFilterViewModel.mFilter.postValue(newFilter)
            } else {
                if (filter.mShowDiscounts) {
                    filter.mShowEvents = !filter.mShowEvents
                    mFilterViewModel.mFilter.postValue(filter)
                }
            }
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mFilterViewModel = ViewModelProviders.of(activity!!).get(FilterViewModel::class.java)
        }
        observeFilters()
    }

    private fun observeFilters() {
        mFilterViewModel.mFilter.observe(viewLifecycleOwner, Observer { filter ->
            filter?.let {
                handleFilterCheck(filter)
            }
        })
    }

    private fun loadFilters() {
        Glide.with(this).load("file:///android_asset/sale.jpg").centerCrop().into(iv_promotions)
        Glide.with(this).load("file:///android_asset/event.png").centerCrop().into(iv_events)
    }

    private fun handleFilterCheck(filterCampaign: FilterCampaign) {
        if (filterCampaign.mShowEvents)
            iv_events_check.visibility = View.VISIBLE
        else
            iv_events_check.visibility = View.GONE
        if (filterCampaign.mShowDiscounts)
            iv_promotions_check.visibility = View.VISIBLE
        else
            iv_promotions_check.visibility = View.GONE
    }

    private fun handleBottomSheetPanel() {
        mBottomSheet = BottomSheetBehavior.from(bs_feed_filters as RelativeLayout)
        mBottomSheet.skipCollapsed = true
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        val shadowFragment = fragmentManager?.findFragmentByTag(BackgroundFragment::class.simpleName)
                as BackgroundFragment
        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, offsetPx: Float) {
                shadowFragment.onClosing(offsetPx)
            }
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_DRAGGING) {
                    mCampaignInteractor.fetchCampaigns()
                }
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