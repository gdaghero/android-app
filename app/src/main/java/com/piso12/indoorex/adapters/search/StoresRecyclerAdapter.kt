package com.piso12.indoorex.adapters.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.adapters.feed.CampaignViewHolder
import com.piso12.indoorex.adapters.feed.FeedDiscountViewHolder
import com.piso12.indoorex.adapters.feed.FeedEventViewHolder
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.fragments.search.stores.OnStoreClickListener
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.interactors.campaign.CampaignReactionInteractor
import com.piso12.indoorex.models.Place
import kotlinx.android.synthetic.main.search_item_promotion.view.*
import kotlinx.android.synthetic.main.search_stores_item.view.*

class StoresRecyclerAdapter(stores: List<Place>, callback: OnStoreClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mStores = stores
    private val mCallback = callback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StoreViewHolder(inflater.inflate(R.layout.search_stores_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mStores.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as StoreViewHolder).bind(mStores[position])
    }

    fun setStores(stores: List<Place>) {
        mStores = stores
        notifyDataSetChanged()
    }

    private inner class StoreViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val item = item

        fun bind(place: Place) {
            item.tv_search_store_name.text = place.mName
            Glide
                .with(item.context)
                .load(place.mImageUrl)
                .circleCrop()
                .into(item.iv_search_store)
            item.setOnClickListener { mCallback.onClick(place) }
        }
    }
}