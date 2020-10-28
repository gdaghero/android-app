package com.piso12.indoorex.fragments.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.viewmodels.FilterViewModel

class BookmarkCampaignFragment : Fragment() {

    private lateinit var mFilterViewModel: FilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bookmark_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        loadBookmarks()
    }

    private fun initializeObservers() {
        activity?.let {
            mFilterViewModel = ViewModelProviders.of(it).get(FilterViewModel::class.java)
        }
    }

    private fun loadBookmarks() {
        mFilterViewModel.mFilter.value = FilterCampaign().also { it.mShowBookmarks = true }
        fragmentManager?.let {
            it.beginTransaction()
                .add(R.id.bookmarks_container, CampaignListFragment(this))
                .commit()
        }
    }
}
