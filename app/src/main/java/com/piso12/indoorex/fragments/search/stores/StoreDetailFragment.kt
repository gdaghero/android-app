package com.piso12.indoorex.fragments.search.stores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.fragments.campaign.CampaignListFragment
import com.piso12.indoorex.fragments.feed.BackgroundFragment
import com.piso12.indoorex.models.FilterCampaign
import com.piso12.indoorex.models.Place
import com.piso12.indoorex.viewmodels.FilterViewModel
import kotlinx.android.synthetic.main.search_store_detail.*
import kotlinx.android.synthetic.main.search_store_detail_body.*
import kotlinx.android.synthetic.main.search_store_detail_header.*
import kotlinx.android.synthetic.main.search_store_detail_header.tv_store_detail_name
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class StoreDetailFragment : Fragment() {

    private lateinit var mStore: Place
    private lateinit var mFilterViewModel: FilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_store_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        loadStoreDetail()
        loadCampaigns()
        handleBackPress()
        handleStoreMap()
    }

    private fun handleStoreMap() {
        btn_store_map.setOnClickListener {
            val storeMap = StoreDetailMapFragment()
            storeMap.arguments = Bundle().also {
                it.putInt("planId", mStore.mPlanId.toInt())
                it.putInt("x", mStore.mXposition.toInt())
                it.putInt("y", mStore.mYposition.toInt())
            }
            fragmentManager?.let {
                it.beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                    .add(R.id.store_detail_coordinator, BackgroundFragment("#DD000000"))
                    .setCustomAnimations(R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                        R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom)
                    .add(R.id.store_detail_coordinator, storeMap)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mFilterViewModel = ViewModelProviders.of(it).get(FilterViewModel::class.java)
        }
    }

    private fun handleBackPress() {
        iv_store_detail_back.setOnClickListener {
            fragmentManager?.let {
                it.popBackStack()
            }
        }
    }

    private fun loadCampaigns() {
        mFilterViewModel.mFilter.value = FilterCampaign().also { it.mStore = mStore }
        val campaignsFragment = CampaignListFragment(this)
        campaignsFragment.arguments = Bundle().also { it.putString("title", "Campañas") }
        fragmentManager?.let {
            it.beginTransaction()
                .add(R.id.store_detail_container, campaignsFragment)
                .commit()
        }
    }

    private fun loadStoreDetail() {
        mStore = arguments?.getSerializable("store") as Place
        Glide
            .with(this)
            .load(mStore.mImageUrl)
            .circleCrop()
            .into(iv_store_detail_logo)
        tv_store_detail_name.text = mStore.mName
        tv_store_detail_description.text =
            mStore.mCategories.map { it.mName }.stream().collect(Collectors.joining(","))
       handleDates()
        mStore.mPhoneNumber?.let { phoneNumber ->
            ll_phone_number.visibility = View.VISIBLE
            tv_store_detail_phone.text = phoneNumber
        }
        tv_store_detail_location.text = "${mStore.mXposition}, ${mStore.mYposition}"
    }

    private fun handleDates() {
        tv_store_detail_dates.text = getOpenDays(mStore.mOpenDays)
        val inputFormatter =
            DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss zZ '(hora estándar de Uruguay)'")
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
        mStore.mOpeningTime?.let { open ->
            var openingTime = ""
            try {
                val openingTimeDate = LocalDateTime.parse(open, inputFormatter)
                openingTime = openingTimeDate.format(outputFormatter)
            } catch (ex: Exception) { }
            ll_time.visibility = View.VISIBLE
            mStore.mClosingTime?.let { close ->
                var closingTime = ""
                try {
                    val closingTimeDate = LocalDateTime.parse(close, inputFormatter)
                    closingTime = closingTimeDate.format(outputFormatter)
                } catch (ex: Exception) { }
                if (openingTime == "" || closingTime == "") {
                    tv_store_detail_times.text = "N/A"
                } else {
                    tv_store_detail_times.text = "$openingTime - $closingTime"
                }
            }
        }
    }

    private fun getOpenDays(days: String): String {
        val days = days.split(";").map{ it.toBoolean() }
        val daysStr = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
        var ret = ""
        for (index in days.indices) {
            if (days[index]) {
                ret += daysStr[index] + ", "
            }
        }
        return if (days.all { it }) {
            "Lun - Dom"
        } else {
            ret.substringBeforeLast(",")
        }
    }
}