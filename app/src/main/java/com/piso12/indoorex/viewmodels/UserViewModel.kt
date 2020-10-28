package com.piso12.indoorex.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piso12.indoorex.dtos.UserDto
import com.piso12.indoorex.models.User

class UserViewModel : ViewModel() {

    var mUser = MutableLiveData<User>()
    var mDummyUser = MutableLiveData<UserDto>()
    var mPassword = MutableLiveData<String>()
    var mPasswordConfirmation = MutableLiveData<String>()

    init {
        mUser.value = null
        mDummyUser.value = UserDto()
        mPassword.value = ""
        mPasswordConfirmation.value = ""
    }
}
