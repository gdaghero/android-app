package com.piso12.indoorex.services.campaign

import android.content.Context
import com.google.gson.Gson
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.dtos.DrawDto
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.campaign.callbacks.OnGetCampaignCallback
import com.piso12.indoorex.services.campaign.callbacks.OnGetCampaignsCallback
import com.piso12.indoorex.services.campaign.callbacks.OnGetDrawCallback
import com.piso12.indoorex.services.campaign.callbacks.OnPostDrawCallback
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.contracts.ICampaignService
import com.piso12.indoorex.services.utils.ServiceCallback
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.create

class CampaignService(context: Context) {

    private val mContext = context

    fun getCampaigns(callback: ServiceCallback<List<CampaignDto>>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICampaignService>()
            .getCampaigns().enqueue(OnGetCampaignsCallback(callback))
    }

    fun getCampaignById(campaignId: Long, callback: ServiceCallback<CampaignDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICampaignService>()
            .getCampaign(campaignId).enqueue(OnGetCampaignCallback(callback))
    }

    fun postDraw(drawId: Number, userId: String, callback: ServiceCallback<DrawDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICampaignService>()
            .postDraw(drawId, userId).enqueue(OnPostDrawCallback(callback))
    }

    fun getDraw(campaignId: Long, callback: ServiceCallback<DrawDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICampaignService>()
            .getDraw(campaignId).enqueue(OnGetDrawCallback(callback))
    }

    fun bookmark(campaignId: Int) {
        val key = "bookmarks_${AuthService.getCurrentUser().uid}"
        val value = ContextHelper.getProperty(mContext, key)
        var bookmarks = JSONArray()
        if (value.isNotEmpty()) {
            bookmarks = JSONArray(value)
            if (!isBookmarked(campaignId)) {
                bookmarks.put(JSONObject().also { it.put("id", campaignId) })
            } else {
                bookmarks.remove(getBookmarkIndex(campaignId, bookmarks))
            }
        }
        ContextHelper.setProperty(mContext, key, bookmarks.toString())
    }

    private fun getBookmarkIndex(campaignId: Int, bookmarks: JSONArray): Int {
        for (index in 0 until bookmarks.length()) {
            val bookmark = bookmarks.getJSONObject(index)
            if (bookmark.get("id") == campaignId) {
                return index
            }
        }
        return -1
    }

    fun isBookmarked(campaignId: Int): Boolean {
        val key = "bookmarks_${AuthService.getCurrentUser().uid}"
        val value = ContextHelper.getProperty(mContext, key)
        var found = false
        if (!value.isNullOrBlank()) {
            val bookmarks = JSONArray(value)
            for (index in 0 until bookmarks.length()) {
                val bookmark = bookmarks.getJSONObject(index)
                if (bookmark.get("id") == campaignId) {
                    found = true
                    break
                }
            }
        }
        return found
    }
}