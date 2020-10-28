package com.piso12.indoorex.adapters.feed

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.interactors.campaign.CampaignReactionInteractor
import kotlinx.android.synthetic.main.feed_item_footer.view.*
import kotlinx.android.synthetic.main.feed_item_header.view.*

abstract class CampaignViewHolder(
    item: View,
    callback: CampaignInteractor.OnCampaignClickListener)
    : RecyclerView.ViewHolder(item) {

    private val mCallback = callback
    private val mView = item

    fun bind(campaign: CampaignDto) {
        mView.tv_feed_store_name.text = campaign.place?.name
        mView.tv_feed_campaign_title.text = campaign.name
        mView.setOnClickListener { mCallback.onClick(campaign) }
        mView.feed_item_options?.setOnClickListener{ mCallback.onOptionsClick(campaign) }
        handleBookmark(campaign)
        handleReactions(campaign)
        bindItem(campaign)
    }

    private fun handleBookmark(campaign: CampaignDto) {
        mView.iv_bookmark.setOnClickListener { mCallback.onBookmark(campaign) }
        if (mCallback.isBookmarked(campaign)) {
            mView.iv_bookmark.setImageDrawable(mView.context
                ?.getDrawable(R.drawable.ic_bookmark_24px))
        } else {
            mView.iv_bookmark.setImageDrawable(mView.context
                ?.getDrawable(R.drawable.ic_bookmark_border_24px))
        }
    }

    private fun handleReactions(campaign: CampaignDto) {
        mView.ib_campaign_like.setOnClickListener { mCallback.onLike(campaign) }
        if (campaign.isLiked) {
            mView.ib_campaign_like.setImageDrawable(mView.context
                ?.getDrawable(R.drawable.ic_favorite_24px))
            mView.ib_campaign_like.setColorFilter(ContextCompat.getColor(mView.context,
                android.R.color.holo_red_light), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            mView.ib_campaign_like.setImageDrawable(mView.context
                ?.getDrawable(R.drawable.ic_favorite_border_24px))
            mView.ib_campaign_like.setColorFilter(ContextCompat.getColor(mView.context,
                R.color.primaryColor), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    abstract fun bindItem(campaign: CampaignDto)
}