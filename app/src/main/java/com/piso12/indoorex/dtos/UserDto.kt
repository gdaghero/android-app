package com.piso12.indoorex.dtos

import com.piso12.indoorex.models.User
import java.io.Serializable

class UserDto : Serializable {

    lateinit var id: String
    lateinit var email: String
    var phoneNumber = ""
    var name = ""
    var lastName = ""
    var gender = ""
    lateinit var birthDate: String
    lateinit var categories: List<CategoryDto>
    lateinit var fcmToken: String

    fun toModel() : User {
        val user = User()
        user.mId = id
        user.mName = name
        user.mLastName = lastName
        user.mEmail = email
        user.mPhoneNumber = phoneNumber
        user.mGender = gender
        user.mBirthDate = birthDate
        user.mCategories = categories.map { it.toModel() }
        return user
    }
}
