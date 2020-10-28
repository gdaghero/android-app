package com.piso12.indoorex.fragments.category

import com.piso12.indoorex.models.Category

interface OnUpdateInterestsListener {
    fun onUpdate(categories: List<Category>)
}