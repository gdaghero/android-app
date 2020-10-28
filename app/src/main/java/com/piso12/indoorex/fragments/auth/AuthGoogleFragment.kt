package com.piso12.indoorex.fragments.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.piso12.indoorex.R
import com.piso12.indoorex.activities.MainActivity
import com.piso12.indoorex.activities.SignUpActivity
import com.piso12.indoorex.exceptions.auth.UserNotFoundException
import com.piso12.indoorex.helpers.ContextHelper


class AuthGoogleFragment(callback: OnAuthResponseCallback) : Fragment() {

    private val mCallback = callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        handleGoogleAuth()
    }

    private fun handleGoogleAuth() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.default_web_client_id))
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        val client = GoogleSignIn.getClient(context!!, options)
        startActivityForResult(client.signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun handleGoogleResponse(googleSignInAccount: Task<GoogleSignInAccount>) {
        val account = googleSignInAccount.result!!
        if (!arguments!!.getBoolean("isLogin")) {
            initSignUpWithGoogle(account)
        } else {
            loginWithGoogle(account)
        }
    }

    private fun initSignUpWithGoogle(account: GoogleSignInAccount) {
        val intent = Intent(context, SignUpActivity::class.java)
        intent.putExtra("email", account.email)
        intent.putExtra("name", account.givenName)
        intent.putExtra("lastName", account.familyName)
        intent.putExtra("token", account.idToken)
        intent.putExtra("provider", AuthProvider.GOOGLE)
        mCallback.onResponseRegister(intent)
    }

    private fun loginWithGoogle(account: GoogleSignInAccount) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(account.email!!, "empty")
            .addOnCompleteListener { task ->
                when (task.exception) {
                    is FirebaseAuthInvalidUserException -> {
                        mCallback.onError(UserNotFoundException())
                    }
                    else -> {
                        signInWithCredential(account.idToken!!)
                    }
                }
            }
    }

    private fun signInWithCredential(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
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
        if (requestCode === GOOGLE_SIGN_IN_REQUEST_CODE) {
            handleGoogleResponse(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 1
    }
}
