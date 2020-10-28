package com.piso12.indoorex.dtos

import java.io.Serializable
import java.util.*

class CampaignDto (

    var id: Long,
    var name: String,
    var description: String,
    var start: Date?,
    var finish: Date?,
    var mediaResource: MediaResourceDto?,
    var place: PlaceDto?,
    var isLiked: Boolean,
    var isDisliked: Boolean,
    var isUsed: Boolean,
    var eventEnabled: Boolean,
    var eventDate: Date?,
    var drawEnabled: Boolean,
    var categories: List<CategoryDto>

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CampaignDto
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}