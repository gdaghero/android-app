package com.piso12.indoorex.dtos

import java.io.Serializable

data class ContentDto(

    val id: Long,
    val title: String,
    val description: String,
    val mediaResource: MediaResourceDto

) : Serializable
