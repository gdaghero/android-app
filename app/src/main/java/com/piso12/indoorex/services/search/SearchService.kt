package com.piso12.indoorex.services.search

import android.content.Context
import com.piso12.indoorex.dtos.SearchResultDto
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.contracts.ISearchService
import com.piso12.indoorex.services.search.callbacks.OnGetSearchCallback
import com.piso12.indoorex.services.utils.ServiceCallback
import retrofit2.create

class SearchService(context: Context) {

    private val mContext = context

    fun search(text: String, callback: ServiceCallback<List<Any>>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ISearchService>()
            .getSearch(text).enqueue(
                OnGetSearchCallback(
                    callback
                )
            )
    }
}