package com.piso12.indoorex.dtos

import com.piso12.indoorex.models.Draw
import java.io.Serializable
import java.util.*

class DrawDto : Serializable {

    var id = 0
    var drawDate: Date ?= null
    var drawDescription = ""
    var mandatoryPresence = false
    var executed = false
    var users = emptyList<UserDto>()
    var winnersIds: String ?= null

    fun toModel() : Draw {
        val draw = Draw()
        draw.mId = id
        draw.mDate = drawDate
        draw.mDescription = drawDescription
        draw.mMandatoryPresence = mandatoryPresence
        draw.mExecuted = executed
        draw.mUsers = users.map { it.toModel() }
        draw.mWinnersIds = winnersIds
        return draw
    }
}
