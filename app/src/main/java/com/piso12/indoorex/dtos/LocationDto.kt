package com.piso12.indoorex.dtos

import java.io.Serializable

data class LocationDto (

    val uid: String,
    val firebaseToken: String,
    val timestamp: String,
    val distances: HashMap<String, Double>,
    val getPosition: Boolean

) : Serializable
