package com.piso12.indoorex.fragments.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.fragments.feed.BackgroundFragment
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.models.Draw
import com.piso12.indoorex.models.User
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.viewmodels.DrawViewModel
import kotlinx.android.synthetic.main.campaign_detail_body.*
import kotlinx.android.synthetic.main.campaign_detail_header.*
import kotlinx.android.synthetic.main.event_detail.*
import kotlinx.android.synthetic.main.event_detail_footer.*
import java.util.*

class EventDetailFragment : Fragment() {

    private lateinit var mBottomSheet: BottomSheetBehavior<RelativeLayout>
    private lateinit var mCampaign: CampaignDto
    private lateinit var mDrawViewModel: DrawViewModel
    private var mDraw: Draw? = null
    private var mIsDrawExpanded = false
    private var mIsParticipating = false
    private lateinit var mCampaignInteractor: CampaignInteractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCampaign()
        initObservers()
        initInteractors()
        handleBottomSheetPanel()
        loadFields()
        handleExpandDraw()
    }

    private fun handleExpandDraw() {
        rl_draw.setOnClickListener {
            if (!mIsDrawExpanded) {
                expandDrawView()
            } else {
                collapseDrawView()
            }
        }
    }

    private fun initCampaign() {
        mCampaign = arguments?.getSerializable("campaign") as CampaignDto
    }

    private fun initObservers() {
        activity?.let {
            mDrawViewModel = ViewModelProviders.of(it).get(DrawViewModel::class.java)
        }
    }

    private fun initInteractors() {
        mCampaignInteractor = CampaignInteractor(this)
        observeDraw()
    }

    private fun observeDraw() {
        mDrawViewModel.mDraw.observe(viewLifecycleOwner, androidx.lifecycle.Observer { draw ->
            draw?.let {
                mDraw = it
                val dummy = User()
                dummy.mId = AuthService.getCurrentUser().uid
                rl_draw.visibility = View.VISIBLE
                mIsParticipating = it.mUsers.contains(dummy)
                if (!mIsParticipating) {
                    expandDrawView()
                } else {
                    collapseDrawView()
                }
            }
        })
    }

    private fun expandDrawView() {
        val drawFragment = EventDrawFragment()
        drawFragment.arguments = Bundle().also { it.putBoolean("isParticipating", mIsParticipating) }
        fragmentManager?.let {
            it.beginTransaction()
            .add(R.id.event_draw_container, drawFragment, EVENT_DRAW_FRAGMENT_TAG)
            .commit()
            iv_draw_state.setImageDrawable(context!!.getDrawable(R.drawable.ic_expand_less_24px))
            mIsDrawExpanded = true
        }
    }

    private fun collapseDrawView() {
        fragmentManager?.let { fm ->
            fm.findFragmentByTag(EVENT_DRAW_FRAGMENT_TAG)?.let { fragment ->
                fm.beginTransaction()
                    .remove(fragment)
                    .commit()
                iv_draw_state.setImageDrawable(context!!.getDrawable(R.drawable.ic_expand_more_24px))
                mIsDrawExpanded = false
            }
        }
    }

    private fun loadFields() {
        loadCampaign()
        loadEvent()
        if (mCampaign.drawEnabled) {
            mCampaignInteractor.fetchDraw(mCampaign.id)
        } else {
            rl_draw.visibility = View.GONE
        }
    }

    private fun loadCampaign() {
        Glide.with(this).load(mCampaign.place?.mediaResource!!.url).circleCrop()
            .into(iv_detail_campaign_store_logo)
        Glide.with(this).load(mCampaign.mediaResource!!.url).centerCrop()
            .into(iv_detail_campaign)
        txt_detail_campaign_title.text = mCampaign.name
        txt_detail_campaign_store.text = mCampaign.place?.name
        txt_detail_campaign_desc.text = mCampaign.description
        tv_event_location.text = mCampaign.place?.name
    }

    private fun loadEvent() {
        val eventDate = Calendar.getInstance()
        eventDate.time = mCampaign.eventDate
        tv_event_date.text = "${ContextHelper.getDayName(eventDate.get(Calendar.DAY_OF_WEEK))}, " +
                "${eventDate.get(Calendar.DAY_OF_MONTH)} de" +
                " ${ContextHelper.getMonthName(eventDate.get(Calendar.MONTH))}"
        tv_event_time.text = "${eventDate.get(Calendar.HOUR_OF_DAY)}" +
                ":${eventDate.get(Calendar.MINUTE)}"
    }

    private fun handleBottomSheetPanel() {
        mBottomSheet = BottomSheetBehavior.from(bs_event_detail as RelativeLayout)
        mBottomSheet.skipCollapsed = true
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            val shadowFragment = fragmentManager?.let {
                it.findFragmentByTag(BackgroundFragment::class.simpleName) as BackgroundFragment
            }!!

            override fun onSlide(view: View, offsetPx: Float) {
                shadowFragment.onClosing(offsetPx)
            }
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    collapseDrawView()
                    fragmentManager?.let {
                        val bg = it.findFragmentByTag(BackgroundFragment::class.simpleName)!!
                        val fr = it.findFragmentByTag(EventDetailFragment::class.simpleName)!!
                        it.beginTransaction().remove(bg).commitNow()
                        it.beginTransaction().remove(fr).commitNow()
                        it.popBackStack()
                    }
                }
            }
        })
    }

    private companion object {
        private const val EVENT_DRAW_FRAGMENT_TAG = "EVENT_DRAW_FRAGMENT_TAG"
    }
}