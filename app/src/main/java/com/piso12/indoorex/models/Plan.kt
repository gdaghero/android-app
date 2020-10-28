package com.piso12.indoorex.models

import java.io.Serializable

class Plan : Serializable {
    lateinit var mId: Number
    lateinit var mVersion: Number
    lateinit var mName: String
    lateinit var mFloorNumber: Number
    lateinit var mImageUrl: String
    lateinit var mWidth: Number
    lateinit var mHeight: Number
}
