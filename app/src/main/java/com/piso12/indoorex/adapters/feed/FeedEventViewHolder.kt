package com.piso12.indoorex.adapters.feed

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.interactors.campaign.CampaignReactionInteractor
import kotlinx.android.synthetic.main.feed_item_body.view.*
import kotlinx.android.synthetic.main.feed_item_footer.view.*
import kotlinx.android.synthetic.main.feed_item_header.view.*

class FeedEventViewHolder(
    item: View,
    callback: CampaignInteractor.OnCampaignClickListener)
    : CampaignViewHolder(item, callback) {

    private val mView = item

    override fun bindItem(campaign: CampaignDto) {
        handleEvent(campaign)
        mView.iv_feed_store_logo.loadLogo(campaign.place?.mediaResource!!.url)
        mView.iv_campaign.loadImage(campaign.mediaResource!!.url)
        mView.tv_feed_campaign_time_remaining.visibility = View.GONE
    }

    private fun handleEvent(campaign: CampaignDto) {
        mView.iv_time.setImageDrawable(
            mView.context.getDrawable(R.drawable.ic_event_24px)
        )
    }

    private fun ImageView.loadLogo(url: String) {
        Glide
            .with(context)
            .load(url)
            .circleCrop()
            .into(this)
    }

    private fun ImageView.loadImage(url: String) {
        Glide
            .with(context)
            .load(url)
            .centerCrop()
            .into(this)
    }
}