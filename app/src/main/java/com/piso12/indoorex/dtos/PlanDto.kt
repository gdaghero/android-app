package com.piso12.indoorex.dtos

import com.piso12.indoorex.models.Plan
import java.io.Serializable

data class PlanDto(
    var id: Long,
    var version: Int,
    var name: String,
    var floor: Int,
    var mediaResource: MediaResourceDto,
    var width: Int,
    var height: Int
) : Serializable {

    fun toModel(): Plan {
        val plan = Plan()
        plan.mFloorNumber = floor
        plan.mName = name
        plan.mVersion = version
        plan.mWidth = width
        plan.mHeight = height
        plan.mId = id
        plan.mImageUrl = mediaResource.url
        return plan
    }

}
