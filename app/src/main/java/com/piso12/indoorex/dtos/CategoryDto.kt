package com.piso12.indoorex.dtos

import com.piso12.indoorex.models.Category
import java.io.Serializable

class CategoryDto : Serializable {

    lateinit var id: Number
    lateinit var name: String
    lateinit var description: String
    lateinit var mediaResource: MediaResourceDto

    fun toModel(): Category {
        val category = Category()
        category.mId = id
        category.mName = name
        category.mDescription = description
        category.mImageUrl = mediaResource.url
        return category
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CategoryDto
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}