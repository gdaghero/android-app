package com.piso12.indoorex.services.category.callbacks

import com.google.common.reflect.TypeToken
import com.piso12.indoorex.dtos.CategoryDto
import com.piso12.indoorex.exceptions.IndoorexException
import com.piso12.indoorex.helpers.JsonParser
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnGetCategoriesCallback(delegate: ServiceCallback<List<Category>>): Callback<ResponseBody> {

    private val mDelegate = delegate

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        mDelegate.onComplete(ServiceResult(t))
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        try {
            val bodyString = response.body()?.string()!!
            val categoriesDtos = JsonParser<CategoryDto>()
                .parseAsList(bodyString, object : TypeToken<List<CategoryDto>>(){}.type)
            val categories = categoriesDtos.map { it.toModel() }
            mDelegate.onComplete(ServiceResult(categories))
        } catch (ex: Exception) {
            mDelegate.onComplete(ServiceResult(IndoorexException()))
        }
    }
}
