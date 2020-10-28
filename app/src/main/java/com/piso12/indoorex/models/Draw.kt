package com.piso12.indoorex.models

import java.io.Serializable
import java.util.*

class Draw : Serializable {

    var mId = 0
    var mDate: Date ?= null
    var mDescription = ""
    var mMandatoryPresence = false
    var mExecuted = false
    var mUsers: List<User> = emptyList()
    var mWinnersIds: String ?= null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Draw
        if (mId != other.mId) return false
        return true
    }

    override fun hashCode(): Int {
        return mId.hashCode()
    }
}