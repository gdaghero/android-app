package com.piso12.indoorex.fragments.search.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.fragments.campaign.CampaignListFragment
import com.piso12.indoorex.fragments.search.stores.StoreFragment
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.viewmodels.FilterViewModel
import kotlinx.android.synthetic.main.search_category_detail.*
import kotlinx.android.synthetic.main.search_category_detail_header.*

class CategoryDetailFragment : Fragment() {

    private lateinit var mFilterViewModel: FilterViewModel
    private lateinit var mCategory: Category

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_category_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        loadCategory()
        loadStores()
        loadCampaigns()
        handleBackPress()
    }

    private fun handleBackPress() {
        iv_category_detail_back.setOnClickListener {
            fragmentManager?.let {
                it.popBackStack()
            }
        }
    }

    private fun loadCategory() {
        mCategory = arguments?.let { it.getSerializable("category") as Category }!!
        tv_category_detail_name.text = mCategory.mName
        tv_category_detail_description.text = mCategory.mDescription
        Glide
            .with(this)
            .load(mCategory.mImageUrl)
            .circleCrop()
            .into(iv_category_detail_logo)
    }

    private fun loadCampaigns() {
        mFilterViewModel.mFilter.value = FilterCampaign().also { it.mCategory = mCategory }
        mFilterViewModel.mSearchResult.value = null
        val campaignsFragment = CampaignListFragment(this)
        campaignsFragment.arguments = Bundle().also { it.putString("title", "Campañas") }
        fragmentManager?.let {
            it.beginTransaction()
                .add(R.id.category_stores_container, campaignsFragment)
                .commit()
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mFilterViewModel = ViewModelProviders.of(it).get(FilterViewModel::class.java)
        }
    }

    private fun loadStores() {
        mFilterViewModel.mFilter.value = FilterCampaign().also { it.mCategory = mCategory }
        fragmentManager?.let {
            it.beginTransaction()
                .add(R.id.category_stores_container, StoreFragment())
                .commit()
        }
    }
}