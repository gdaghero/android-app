package com.piso12.indoorex.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piso12.indoorex.models.Category

class CategoryViewModel : ViewModel() {

    var mCategories = MutableLiveData<List<Category>>()
    var mSelectedCategories = MutableLiveData<List<Category>>()

    init {
        mCategories.value = emptyList()
        mSelectedCategories.value = emptyList()
    }
}