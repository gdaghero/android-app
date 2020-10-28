package com.piso12.indoorex.models

import com.piso12.indoorex.dtos.UserDto
import java.io.Serializable

class User : Serializable {

    lateinit var mId: String
    lateinit var mName: String
    lateinit var mLastName: String
    lateinit var mEmail: String
    lateinit var mPhoneNumber: String
    lateinit var mGender: String
    lateinit var mBirthDate: String
    lateinit var mCategories: List<Category>

    fun toDto() : UserDto {
        val userDto = UserDto()
        userDto.id = mId
        userDto.name = mName
        userDto.lastName = mLastName
        userDto.email = mEmail
        userDto.phoneNumber = mPhoneNumber
        userDto.gender = mGender
        userDto.birthDate = mBirthDate
        userDto.categories = mCategories.map { it.toDto() }
        return userDto
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        if (mId != other.mId) return false
        return true
    }

    override fun hashCode(): Int {
        return mId.hashCode()
    }
}