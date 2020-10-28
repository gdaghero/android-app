package com.piso12.indoorex.interactors

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.exceptions.auth.AuthExceptionHandler
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.services.category.CategoryService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.CategoryViewModel

object CategoryInteractor {

    private lateinit var mCategoryViewModel: CategoryViewModel
    private lateinit var mCategoryService: CategoryService
    private lateinit var mContext: FragmentActivity
    private lateinit var mContainer: View
    private const val TAG = "CategoryInteractor"

    fun getCategories() {
        mCategoryService.getCategories(onGetCategoriesResponse())
    }

    private fun onGetCategoriesResponse(): ServiceCallback<List<Category>> {
        return object : ServiceCallback<List<Category>> {
            override fun onComplete(result: ServiceResult<List<Category>>) {
                if (result.isSuccessful) {
                    mCategoryViewModel.mCategories.postValue(result.result)
                }
            }
        }
    }

    fun postCategories(categories: List<Category>) {
        val userId = AuthService.getCurrentUser().uid
        mCategoryService.postCategories(categories, userId, onPostCategoriesResponse())
    }

    private fun onPostCategoriesResponse() = object : ServiceCallback<List<Category>> {
            override fun onComplete(result: ServiceResult<List<Category>>) {
                if (result.isSuccessful) {
                    mCategoryViewModel.mSelectedCategories.postValue(result.result)
                } else {
                    AuthExceptionHandler.getInstance(mContext).showError(
                        mContainer,
                        TAG, result.error!!)
                }
            }
    }

    fun initialize(context: FragmentActivity, container: View) {
        mContext = context
        mContainer = container
        mCategoryService = CategoryService(context)
        mCategoryViewModel = ViewModelProviders.of(context).get(CategoryViewModel::class.java)
    }
}