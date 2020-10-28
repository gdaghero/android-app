package com.piso12.indoorex.fragments.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.piso12.indoorex.R
import com.piso12.indoorex.adapters.feed.FeedCampaignAdapter
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.models.Place
import com.piso12.indoorex.viewmodels.CampaignViewModel
import com.piso12.indoorex.viewmodels.FilterViewModel
import kotlinx.android.synthetic.main.campaigns_list.*

class CampaignListFragment(parent: Fragment) : Fragment() {

    private lateinit var mCampaignViewModel: CampaignViewModel
    private lateinit var mFilterViewModel: FilterViewModel
    private lateinit var mCampaignInteractor: CampaignInteractor
    private val mParent = parent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.campaigns_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeInteractors()
        initializeObservers()
        setCampaignsAdapter()
        loadCampaigns()
        loadTitle()
    }

    private fun loadTitle() {
        arguments?.let { args ->
            args.getString("title")?.let {
                rv_campaigns_title.visibility = View.VISIBLE
                rv_campaigns_title.text = it
            }
        }
    }

    fun fetchCampaigns() {
        mCampaignInteractor.fetchCampaigns()
    }

    private fun loadCampaigns() {
        mCampaignViewModel.mCampaigns.value?.let {
            if (it.isEmpty()) {
                fetchCampaigns()
            } else {
                filterCampaigns(it)
            }
        }
    }

    private fun initializeInteractors() {
        mCampaignInteractor = CampaignInteractor(mParent)
    }

    private fun initializeObservers() {
        activity?.let {
            mCampaignViewModel = ViewModelProviders.of(it).get(CampaignViewModel::class.java)
            mFilterViewModel = ViewModelProviders.of(it).get(FilterViewModel::class.java)
        }
        observeCampaigns()
    }

    private fun observeCampaigns() {
        mCampaignViewModel.mCampaigns.observe(viewLifecycleOwner, Observer { campaigns ->
            (rv_campaigns.adapter as FeedCampaignAdapter).setCampaigns(
                filterCampaigns(campaigns)
            )
        })
        mFilterViewModel.mSearchResult.observe(viewLifecycleOwner, Observer { searchResult ->
            searchResult?.let {
                (rv_campaigns.adapter as FeedCampaignAdapter).setCampaigns(
                    it.mCampaigns
                )
            }
        })
    }

    private fun filterCampaigns(campaigns: List<CampaignDto>): List<CampaignDto> {
        mFilterViewModel.mFilter.value?.let { filter ->
            return campaigns
                .asSequence()
                .filter { filterByBookmark(filter, it) }
                .filter { filterByEvents(filter, it) || filterByDiscounts(filter, it) }
                .filter { filterByStore(filter, it) }
                .filter { filterByCategory(filter, it) }
                .toList()
        }
        return campaigns
    }

    private fun filterByBookmark(filter: FilterCampaign, campaign: CampaignDto): Boolean {
        if (filter.mShowBookmarks) {
            return mCampaignInteractor.isBookmarked(campaign.id)
        }
        return true
    }

    private fun filterByStore(filter: FilterCampaign, campaign: CampaignDto): Boolean {
        if (filter.mStore != null) {
            return campaign.place!!.id == filter.mStore!!.mId
        }
        return true
    }

    private fun filterByCategory(filter: FilterCampaign, campaign: CampaignDto): Boolean {
        if (filter.mCategory != null) {
            return campaign.categories.contains(filter.mCategory!!.toDto())
        }
        return true
    }

    private fun filterByEvents(filter: FilterCampaign, campaign: CampaignDto): Boolean {
        return filter.mShowEvents && campaign.eventEnabled
    }

    private fun filterByDiscounts(filter: FilterCampaign, campaign: CampaignDto): Boolean {
        return filter.mShowDiscounts && !campaign.eventEnabled
    }

    private fun setCampaignsAdapter() {
        val adapter = FeedCampaignAdapter(listOf(), mCampaignInteractor.onCampaignClickListener())
        adapter.setHasStableIds(true)
        rv_campaigns.setHasFixedSize(true)
        rv_campaigns.layoutManager = LinearLayoutManager(context)
        rv_campaigns.adapter = adapter
    }
}