package com.piso12.indoorex.services.user

import android.content.Context
import com.piso12.indoorex.dtos.UserDto
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.models.User
import com.piso12.indoorex.services.retrofit.RetrofitClientBackoffice
import com.piso12.indoorex.services.retrofit.contracts.IUserService
import com.piso12.indoorex.services.user.callbacks.OnEditUserCallback
import com.piso12.indoorex.services.user.callbacks.OnGetUserCallback
import com.piso12.indoorex.services.user.callbacks.OnPostUserCallback
import com.piso12.indoorex.services.utils.ServiceCallback
import retrofit2.create

class UserService(context: Context) {

    private val mContext = context

    fun postUser(userDto: UserDto, callback: ServiceCallback<String>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<IUserService>()
            .postUser(userDto).enqueue(OnPostUserCallback(callback))
    }

    fun getUser(userId: String, callback: ServiceCallback<UserDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<IUserService>()
            .getUser(userId).enqueue(OnGetUserCallback(callback))
    }

    fun editUser(user: UserDto, callback: ServiceCallback<UserDto>) {
        RetrofitClientBackoffice
            .get(ContextHelper.getBackofficeApiUrl(mContext))
            .create<IUserService>()
            .editUser(user.id, user).enqueue(OnEditUserCallback(callback))
    }
}
