package com.piso12.indoorex.interactors

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.dtos.UserDto
import com.piso12.indoorex.exceptions.auth.AuthExceptionHandler
import com.piso12.indoorex.models.User
import com.piso12.indoorex.services.user.UserService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.UserViewModel

object UserInteractor {

    private lateinit var mContext: FragmentActivity
    private lateinit var mContainer: View
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mUserService: UserService
    private const val TAG = "UserInteractor"

    fun fetchUser(userId: String) {
        if (mUserViewModel.mUser.value == null) {
            mUserService.getUser(userId, onGetUserResponse())
        }
    }

    private fun onGetUserResponse(): ServiceCallback<UserDto> {
        return object : ServiceCallback<UserDto> {
            override fun onComplete(result: ServiceResult<UserDto>) {
                if (result.isSuccessful) {
                    mUserViewModel.mUser.postValue(result.result?.toModel())
                } else {
                    AuthExceptionHandler.getInstance(mContext)
                        .showError(mContainer,
                            TAG, result.error!!)
                }
            }
        }
    }

    fun editUser(user: UserDto) {
        mUserService.editUser(user, onEditUserResponse())
    }

    private fun onEditUserResponse(): ServiceCallback<UserDto> {
        return object : ServiceCallback<UserDto> {
            override fun onComplete(result: ServiceResult<UserDto>) {
                if (result.isSuccessful) {
                    mUserViewModel.mUser.postValue(result.result?.toModel())
                } else {
                    AuthExceptionHandler.getInstance(mContext)
                        .showError(mContainer,
                            TAG, result.error!!)
                }
            }

        }
    }

    fun initialize(context: FragmentActivity, container: View) {
        mContext = context
        mContainer = container
        mUserViewModel = ViewModelProviders.of(context).get(UserViewModel::class.java)
        mUserService = UserService(context)
    }
}