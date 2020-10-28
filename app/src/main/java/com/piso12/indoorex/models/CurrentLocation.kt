package com.piso12.indoorex.models

import java.io.Serializable

data class CurrentLocation(
    val placeId: Number,
    val name: String,
    val x: Number,
    val y: Number,
    var planId: Number
) : Serializable