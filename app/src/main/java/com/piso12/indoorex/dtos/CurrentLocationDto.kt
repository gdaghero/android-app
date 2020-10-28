package com.piso12.indoorex.dtos

import com.piso12.indoorex.models.CurrentLocation
import java.io.Serializable

class CurrentLocationDto : Serializable {

    lateinit var placeId: Number
    lateinit var placeName: String
    lateinit var x: Number
    lateinit var y: Number
    lateinit var planId: Number

    fun toModel(): CurrentLocation {
        return CurrentLocation(
            placeId,
            placeName,
            x,
            y,
            planId
        )
    }
}