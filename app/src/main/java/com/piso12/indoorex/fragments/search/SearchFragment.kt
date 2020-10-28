package com.piso12.indoorex.fragments.search

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.fragments.campaign.CampaignListFragment
import com.piso12.indoorex.fragments.search.categories.CategoryFragment
import com.piso12.indoorex.fragments.search.stores.StoreFragment
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.interactors.SearchInteractor
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.viewmodels.CampaignViewModel
import com.piso12.indoorex.viewmodels.FilterViewModel
import com.piso12.indoorex.viewmodels.PlaceViewModel
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_input.*

class SearchFragment : Fragment() {

    private lateinit var mFilterViewModel: FilterViewModel
    private lateinit var mCampaignViewModel: CampaignViewModel
    private lateinit var mPlaceViewModel: PlaceViewModel
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var mSearchText: String
    private lateinit var mSearchInteractor: SearchInteractor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeInteractors()
        initializeObservers()
        showStores()
        showCategories()
        handleSearchClick()
        handleCloseSearchClick()
        handleTextChange()
    }

    private fun initializeInteractors() {
        mSearchInteractor = SearchInteractor(this)
    }

    private fun handleCloseSearchClick() {
        ib_search_close.setOnClickListener {
            mCampaignViewModel.mCampaigns.postValue(mCampaignViewModel.mCampaigns.value)
            mPlaceViewModel.mPlaces.postValue(mPlaceViewModel.mPlaces.value)
            txt_search_input.setText("")
            hideCampaigns()
            showCategories()
            search_coordinator.requestFocus()
        }
    }

    private fun hideCampaigns() {
        fragmentManager?.let {
            it.findFragmentByTag(CampaignListFragment::class.simpleName)?.let { fr ->
                it.beginTransaction().remove(fr).commit()
            }
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mFilterViewModel = ViewModelProviders.of(it).get(FilterViewModel::class.java)
            mCampaignViewModel = ViewModelProviders.of(it).get(CampaignViewModel::class.java)
            mPlaceViewModel = ViewModelProviders.of(it).get(PlaceViewModel::class.java)
        }
        mFilterViewModel.mSearchResult.observe(viewLifecycleOwner, Observer { searchResult ->
            searchResult?.let {
                ib_search_close.visibility = View.VISIBLE
                search_progress.visibility = View.GONE
                showCampaigns()
            }
        })
    }

    private fun showCampaigns() {
        fragmentManager?.let {
            it.beginTransaction()
                .add(
                    R.id.search_fragments_container,
                    CampaignListFragment(this),
                    CampaignListFragment::class.simpleName)
                .commit()
        }
    }

    private fun handleSearchClick() {
        txt_search_input.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                ib_search_close.visibility = View.VISIBLE
            } else {
                mFilterViewModel.mFilter.value = FilterCampaign()
                mFilterViewModel.mSearchResult.value = null
                ib_search_close.visibility = View.GONE
                ContextHelper.hideSoftKeyboard(activity!!, v)
            }
        }
    }

    private fun showStores() {
        fragmentManager?.let {
            it.beginTransaction()
                .add(
                    R.id.search_fragments_container,
                    StoreFragment(),
                    StoreFragment::class.simpleName)
                .commit()
        }
    }

    private fun showCategories() {
        fragmentManager?.let {
            it.beginTransaction()
                    .add(
                        R.id.search_fragments_container,
                        CategoryFragment(),
                        CategoryFragment::class.simpleName)
                    .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        mFilterViewModel.mSearchResult.value = null
    }

    private fun handleTextChange() {
        txt_search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Nothing to do here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing to do here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotBlank()) {
                    ib_search_close.visibility = View.GONE
                    handleTextChange(s.toString())
                } else {
                    ib_search_close.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun handleTextChange(searchText: String) {
        mSearchText = searchText
        search_progress.visibility = View.VISIBLE
        if (!::mHandler.isInitialized) {
            initializeSearchHandler()
        } else {
            mHandler.removeCallbacks(mRunnable)
        }
        mHandler.postDelayed(mRunnable, START_SEARCH_DELAY)
    }

    private fun initializeSearchHandler() {
        mHandler = Handler()
        mRunnable = Runnable { mSearchInteractor.search(mSearchText) }
    }

    private companion object {
        private const val START_SEARCH_DELAY = 800L
    }
}
