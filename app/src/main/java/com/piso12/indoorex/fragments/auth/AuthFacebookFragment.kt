package com.piso12.indoorex.fragments.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.piso12.indoorex.activities.MainActivity
import com.piso12.indoorex.activities.SignUpActivity
import com.piso12.indoorex.exceptions.auth.LoginFacebookException
import com.piso12.indoorex.exceptions.auth.UserNotFoundException
import org.json.JSONObject

class AuthFacebookFragment(callback: OnAuthResponseCallback) : Fragment() {

    private val mCallback = callback
    private lateinit var mFacebookCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        handleFacebookAuth()
    }

    private fun handleFacebookAuth() {
        mFacebookCallbackManager = CallbackManager.Factory.create()
        val loginManager = LoginManager.getInstance()
        loginManager.registerCallback(mFacebookCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                requestExtraFields(result)
            }
            override fun onCancel() {
                mCallback.onCancel()
            }
            override fun onError(error: FacebookException?) {
                mCallback.onError(LoginFacebookException())
            }
        })
        loginManager.loginBehavior = LoginBehavior.DIALOG_ONLY
        loginManager.logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    private fun requestExtraFields(result: LoginResult?) {
        val request = GraphRequest.newMeRequest(result!!.accessToken) { obj, _ ->
            handleFacebookResponse(result.accessToken, obj)
        }
        val parameters = Bundle()
        parameters.putString("fields", "first_name, last_name, email, birthday, picture")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleFacebookResponse(accessToken: AccessToken?, jsonObject: JSONObject) {
        if (!arguments!!.getBoolean("isLogin")) {
            initSignUpWithFacebook(jsonObject, accessToken!!.token)
        } else {
            loginWithFacebook(accessToken!!.token, jsonObject.getString("email"))
        }
    }

    private fun initSignUpWithFacebook(jsonObject: JSONObject, token: String) {
        val intent = Intent(context, SignUpActivity::class.java)
        intent.putExtra("email", jsonObject.getString("email"))
        intent.putExtra("name", jsonObject.getString("first_name"))
        intent.putExtra("lastName", jsonObject.getString("last_name"))
        intent.putExtra("token", token)
        intent.putExtra("provider", AuthProvider.FACEBOOK)
        mCallback.onResponseRegister(intent)
    }

    private fun loginWithFacebook(token: String, email: String) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, "empty")
            .addOnCompleteListener { task ->
                when (task.exception) {
                    is FirebaseAuthInvalidUserException -> {
                        mCallback.onError(UserNotFoundException())
                    }
                    else -> {
                        signInWithCredential(token)
                    }
                }
            }
    }

    private fun signInWithCredential(token: String) {
        val credential = FacebookAuthProvider.getCredential(token)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mUser = FirebaseAuth.getInstance().currentUser
                    mUser!!.getIdToken(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                mCallback.onResponseLogin(
                                    Intent(context, MainActivity::class.java),
                                    task.result!!.token,
                                    task.result!!.expirationTimestamp
                                )
                            } else {
                                task.exception?.let { mCallback.onError(it) }
                            }
                        }
                } else {
                    task.exception?.let { mCallback.onError(it) }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
