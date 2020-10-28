package com.piso12.indoorex.dtos

import com.piso12.indoorex.models.Place
import java.io.Serializable

class PlaceDto : Serializable {

    lateinit var id: Number
    lateinit var name: String
    lateinit var description: String
    lateinit var mediaResource: MediaResourceDto
    lateinit var categories: List<CategoryDto>
    var phoneNumber: String? = null
    lateinit var openDays: String
    var closingTime: String? = null
    var openingTime: String? = null
    lateinit var xposition: Number
    lateinit var yposition: Number
    lateinit var planId: Number

    fun toModel(): Place {
        val place = Place()
        place.mId = id
        place.mName = name
        place.mDescription = description
        place.mImageUrl = mediaResource.url
        place.mCategories = categories.map { it.toModel() }
        place.mOpenDays = openDays
        place.mXposition = xposition
        place.mYposition = yposition
        place.mPlanId = planId
        place.mPhoneNumber = phoneNumber
        place.mClosingTime = closingTime
        place.mOpeningTime = openingTime
        return place
    }
}
