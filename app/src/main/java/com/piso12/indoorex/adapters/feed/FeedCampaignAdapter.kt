package com.piso12.indoorex.adapters.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.interactors.campaign.CampaignReactionInteractor

class FeedCampaignAdapter(
    campaigns: List<CampaignDto>,
    callback: CampaignInteractor.OnCampaignClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mCampaigns = campaigns
    private val mCallback = callback

    fun setCampaigns(campaigns: List<CampaignDto>) {
        mCampaigns = campaigns
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_DISCOUNT) {
            FeedDiscountViewHolder(
                inflater.inflate(R.layout.feed_item, parent, false),
                mCallback
            )
        } else {
            FeedEventViewHolder(
                inflater.inflate(R.layout.feed_item_event, parent, false),
                mCallback
            )
        }
    }

    override fun getItemCount(): Int {
        return mCampaigns.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (mCampaigns[position].eventEnabled) {
            TYPE_EVENT
        } else {
            TYPE_DISCOUNT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CampaignViewHolder).bind(mCampaigns[position])
    }

    private companion object {
        private const val TYPE_DISCOUNT = 1
        private const val TYPE_EVENT = 2
    }
}
