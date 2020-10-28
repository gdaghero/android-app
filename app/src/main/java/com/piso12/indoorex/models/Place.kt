package com.piso12.indoorex.models

import java.io.Serializable

class Place : Serializable {

    lateinit var mPlanId: Number
    lateinit var mYposition: Number
    lateinit var mXposition: Number
    var mOpeningTime: String? = null
    var mClosingTime: String? = null
    lateinit var mOpenDays: String
    var mPhoneNumber: String? = null
    lateinit var mId: Number
    lateinit var mName: String
    lateinit var mDescription: String
    lateinit var mImageUrl: String
    lateinit var mCategories: List<Category>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Place
        if (mId != other.mId) return false
        return true
    }

    override fun hashCode(): Int {
        return mId.hashCode()
    }
}