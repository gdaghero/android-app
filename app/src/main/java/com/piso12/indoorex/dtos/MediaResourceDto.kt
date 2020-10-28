package com.piso12.indoorex.dtos

import java.io.Serializable

data class MediaResourceDto(

    var id: Long,
    var name: String,
    var mediaType: String,
    var uid: String,
    var url: String

) : Serializable
