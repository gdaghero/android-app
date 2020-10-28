package com.piso12.indoorex.fragments.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.fragments.feed.BackgroundFragment
import com.piso12.indoorex.helpers.ContextHelper
import kotlinx.android.synthetic.main.campaign_detail.*
import kotlinx.android.synthetic.main.campaign_detail_body.*
import kotlinx.android.synthetic.main.campaign_detail_footer.*
import kotlinx.android.synthetic.main.campaign_detail_header.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class DiscountDetailFragment : Fragment() {

    private lateinit var mBottomSheet: BottomSheetBehavior<RelativeLayout>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.campaign_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleBottomSheetPanel()
        loadCampaign()
    }

    private fun loadCampaign() {
        val campaign = arguments?.getSerializable("campaign") as CampaignDto
        loadFields(campaign)
        btn_show_qr.setOnClickListener {
            onQrClick(campaign)
        }
    }

    private fun onQrClick(campaign: CampaignDto) {
        val qrFragment = CampaignQrFragment()
        qrFragment.arguments = Bundle().also { it.putSerializable("campaign", campaign) }
        fragmentManager?.let {
            it.beginTransaction()
                .setCustomAnimations(
                    R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                    R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom)
                .add(view!!.id, qrFragment, CampaignQrFragment::class.simpleName)
                .commit()
        }
    }

    private fun loadFields(campaign: CampaignDto) {
        Glide.with(this).load(campaign.place?.mediaResource!!.url).circleCrop()
            .into(iv_detail_campaign_store_logo)
        Glide.with(this).load(campaign.mediaResource!!.url).centerCrop()
            .into(iv_detail_campaign)
        txt_detail_campaign_title.text = campaign.name
        txt_detail_campaign_store.text = campaign.place?.name
        txt_detail_campaign_desc.text = campaign.description
        val discountDate = Calendar.getInstance()
        discountDate.time = campaign.finish
        tv_discount_date.text = "Valida hasta el ${ContextHelper.getDayName(discountDate.get(Calendar.DAY_OF_WEEK))}, " +
                "${discountDate.get(Calendar.DAY_OF_MONTH)} de" +
                " ${ContextHelper.getMonthName(discountDate.get(Calendar.MONTH))}"
        tv_campaign_detail_time_remaining.text = getTimeRemaining(campaign)
    }

    private fun getTimeRemaining(campaign: CampaignDto): String {
        val remainingTime = Duration.between(
            LocalDateTime.ofInstant(campaign.start?.toInstant(), ZoneId.systemDefault()),
            LocalDateTime.ofInstant(campaign.finish?.toInstant(), ZoneId.systemDefault())
        ).toMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(remainingTime)
        return if (hours >= 1) {
            "${hours}h"
        } else {
            "${TimeUnit.MILLISECONDS.toMinutes(remainingTime)}m"
        }
    }

    private fun handleBottomSheetPanel() {
        mBottomSheet = BottomSheetBehavior.from(bs_discount_detail as RelativeLayout)
        mBottomSheet.skipCollapsed = true
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            val mShadowFragment = fragmentManager?.let {
                it.findFragmentByTag(BackgroundFragment::class.simpleName) as BackgroundFragment
            }!!

            override fun onSlide(view: View, offsetPx: Float) {
                mShadowFragment.onClosing(offsetPx)
            }
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    fragmentManager?.let {
                        val bg = it.findFragmentByTag(BackgroundFragment::class.simpleName)!!
                        val fr = it.findFragmentByTag(DiscountDetailFragment::class.simpleName)!!
                        it.beginTransaction().remove(bg).commitNow()
                        it.beginTransaction().remove(fr).commitNow()
                        it.popBackStack()
                    }
                }
            }
        })
    }
}