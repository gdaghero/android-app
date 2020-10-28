package com.piso12.indoorex.adapters.feed

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.interactors.campaign.CampaignReactionInteractor
import kotlinx.android.synthetic.main.feed_item_body.view.*
import kotlinx.android.synthetic.main.feed_item_footer.view.*
import kotlinx.android.synthetic.main.feed_item_header.view.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class FeedDiscountViewHolder(
    item: View,
    callback: CampaignInteractor.OnCampaignClickListener)
    : CampaignViewHolder(item, callback) {

    private val mView = item

    override fun bindItem(campaign: CampaignDto) {
        mView.iv_campaign.loadImage(campaign.mediaResource!!.url)
        mView.iv_feed_store_logo.loadLogo(campaign.place?.mediaResource!!.url)
        handleTimeRemaining(campaign)
    }

    private fun handleTimeRemaining(campaign: CampaignDto) {
        val remainingTime = Duration.between(
            LocalDateTime.ofInstant(campaign.start?.toInstant(), ZoneId.systemDefault()),
            LocalDateTime.ofInstant(campaign.finish?.toInstant(), ZoneId.systemDefault())
        ).toMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(remainingTime)
        if (hours >= 1) {
            mView.tv_feed_campaign_time_remaining.text = "${hours}h"
        } else {
            mView.tv_feed_campaign_time_remaining.text =
                "${TimeUnit.MILLISECONDS.toMinutes(remainingTime)}m"
        }
    }

    private fun ImageView.loadImage(url: String) {
        Glide
            .with(context)
            .load(url)
            .centerCrop()
            .into(this)
    }

    private fun ImageView.loadLogo(url: String) {
        Glide
            .with(context)
            .load(url)
            .circleCrop()
            .into(this)
    }
}