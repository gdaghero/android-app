package com.piso12.indoorex.fragments.search.stores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.piso12.indoorex.R
import com.piso12.indoorex.adapters.search.StoresRecyclerAdapter
import com.piso12.indoorex.interactors.PlaceInteractor
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.models.Place
import com.piso12.indoorex.viewmodels.FilterViewModel
import com.piso12.indoorex.viewmodels.PlaceViewModel
import kotlinx.android.synthetic.main.search_stores.*

class StoreFragment : Fragment() {

    private lateinit var mPlaceViewModel: PlaceViewModel
    private lateinit var mFilterViewModel: FilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_stores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeInteractors()
        initializeObservers()
        initializeAdapter()
        fetchStores()
    }

    override fun onPause() {
        super.onPause()
        PlaceInteractor.removeCallbacks()
    }

    private fun initializeInteractors() {
        activity?.let {
            PlaceInteractor.initialize(it)
        }
    }

    private fun initializeAdapter() {
        val adapter = StoresRecyclerAdapter(
            listOf(),
            onStoreClickListener()
        )
        adapter.setHasStableIds(true)
        rv_stores.setHasFixedSize(true)
        rv_stores.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_stores.adapter = adapter
    }

    private fun onStoreClickListener() : OnStoreClickListener {
        return object : OnStoreClickListener {
            override fun onClick(store: Place) {
                handleStoreClick(store)
            }
        }
    }

    private fun handleStoreClick(store: Place) {
        val storeDetail = StoreDetailFragment()
        storeDetail.arguments = Bundle().also { it.putSerializable("store", store) }
        fragmentManager?.let {
            it.beginTransaction()
                .setCustomAnimations(R.anim.anim_slide_from_left_to_origin, R.anim.anim_slide_from_right_to_origin)
                .add(R.id.search_coordinator, storeDetail)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchStores() {
        mPlaceViewModel.mPlaces.value?.let {
            if (it.isEmpty()) {
                PlaceInteractor.getPlaces()
            } else {
                filterStores(it)
            }
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mPlaceViewModel = ViewModelProviders.of(it).get(PlaceViewModel::class.java)
            mFilterViewModel = ViewModelProviders.of(it).get(FilterViewModel::class.java)
        }
        mPlaceViewModel.mPlaces.observe(viewLifecycleOwner, Observer { places ->
            places?.let { it ->
                (rv_stores.adapter as StoresRecyclerAdapter).setStores(
                    filterStores(it)
                )
            }
        })
        mFilterViewModel.mSearchResult.observe(viewLifecycleOwner, Observer { searchResult ->
            searchResult?.let {
                (rv_stores.adapter as StoresRecyclerAdapter).setStores(
                    it.mStores
                )
            }
        })
    }

    private fun filterStores(stores: List<Place>): List<Place> {
        mFilterViewModel.mFilter.value?.let { filter ->
            return stores
                .filter { filterByCategory(filter, it) }
        }
        return stores
    }

    private fun filterByCategory(filter: FilterCampaign, place: Place): Boolean {
        if (filter.mCategory != null) {
            return place.mCategories.contains(filter.mCategory!!)
        }
        return true
    }
}
