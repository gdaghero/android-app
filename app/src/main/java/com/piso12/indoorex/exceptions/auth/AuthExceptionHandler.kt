package com.piso12.indoorex.exceptions.auth

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.piso12.indoorex.R
import com.piso12.indoorex.exceptions.ExceptionHandler

abstract class AuthExceptionHandler(context: Context) : ExceptionHandler(context) {

    private val mContext = context

    override fun showError(container: View, tag: String, error: Throwable) {
        val message = when (error) {
            is SignUpUserAlreadyExistsException ->
                mContext.getString(R.string.sign_up_user_already_exists_error)
            is LoginEmptyFieldsException ->
                mContext.getString(R.string.login_empty_email_or_password_error)
            is FirebaseAuthWeakPasswordException ->
                mContext.getString(R.string.login_error_weak_password)
            is LoginFireBaseAuthException ->
                mContext.getString(R.string.login_error_firebase_auth)
            is FirebaseAuthUserCollisionException ->
                mContext.getString(R.string.sign_up_user_already_exists_error)
            is FirebaseAuthInvalidCredentialsException ->
                mContext.getString(R.string.sign_up_firebase_auth_error)
            is SignUpBackofficeException ->
                mContext.getString(R.string.sign_up_backoffice_error)
            is SignUpFirebaseException ->
                mContext.getString(R.string.sign_up_firebase_error)
            is UserNotFoundException ->
                "El usuario no se encuentra registrado"
            else -> ""
        }
        if (message == "") {
            super.showError(container, tag, error)
        } else {
            Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
        }
    }



    companion object {

        private lateinit var mInstance: ExceptionHandler

        fun getInstance(context: FragmentActivity): ExceptionHandler {
            if (!::mInstance.isInitialized) {
                mInstance = object : AuthExceptionHandler(context) { }
            }
            return mInstance
        }
    }
}
