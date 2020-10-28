package com.piso12.indoorex.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piso12.indoorex.models.Draw

class DrawViewModel : ViewModel() {

    var mDraw = MutableLiveData<Draw>()
    var mDraws = MutableLiveData<MutableList<Draw>>()

    init {
        mDraws.value = mutableListOf()
    }
}