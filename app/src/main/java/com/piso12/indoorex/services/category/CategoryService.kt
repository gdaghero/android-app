package com.piso12.indoorex.services.category

import android.content.Context
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.services.category.callbacks.OnGetCategoriesCallback
import com.piso12.indoorex.services.category.callbacks.OnPostCategoriesCallback
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.contracts.ICategoryService
import com.piso12.indoorex.services.utils.ServiceCallback
import retrofit2.create

class CategoryService(context: Context) {

    private val mContext = context

    fun getCategories(callback: ServiceCallback<List<Category>>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICategoryService>()
            .getCategories().enqueue(OnGetCategoriesCallback(callback))
    }

    fun postCategories(categories: List<Category>,
                       userId: String, callback: ServiceCallback<List<Category>>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<ICategoryService>()
            .postCategories(userId, categories.map { it.toDto() })
            .enqueue(OnPostCategoriesCallback(callback))
    }
}
