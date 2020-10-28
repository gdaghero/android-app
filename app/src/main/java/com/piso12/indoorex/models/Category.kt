package com.piso12.indoorex.models

import com.piso12.indoorex.dtos.CategoryDto
import java.io.Serializable

class Category : Serializable {

    lateinit var mId: Number
    lateinit var mName: String
    lateinit var mDescription: String
    lateinit var mImageUrl: String

    fun toDto(): CategoryDto {
        val categoryDto = CategoryDto()
        categoryDto.id = mId
        categoryDto.name = mName
        categoryDto.description = mDescription
        return categoryDto
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Category
        if (mId != other.mId) return false
        return true
    }

    override fun hashCode(): Int {
        return mId.hashCode()
    }

}