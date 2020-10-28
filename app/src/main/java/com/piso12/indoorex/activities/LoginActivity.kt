package com.piso12.indoorex.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.piso12.indoorex.R
import com.piso12.indoorex.animations.FlipAnimation
import com.piso12.indoorex.exceptions.auth.AuthExceptionHandler
import com.piso12.indoorex.exceptions.auth.LoginEmptyFieldsException
import com.piso12.indoorex.exceptions.auth.LoginFireBaseAuthException
import com.piso12.indoorex.exceptions.auth.SignUpUserAlreadyExistsException
import com.piso12.indoorex.fragments.auth.AuthFacebookFragment
import com.piso12.indoorex.fragments.auth.AuthGoogleFragment
import com.piso12.indoorex.fragments.auth.AuthProvider
import com.piso12.indoorex.fragments.auth.OnAuthResponseCallback
import com.piso12.indoorex.helpers.ContextHelper
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.util_progress_bar.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        handleLoginOrRegistration()
        handleLogoVisibility()
        handleFacebookAuth()
        handleGoogleAuth()
    }

    private fun handleFacebookAuth() {
        btn_login_or_register_facebook.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            val fragment = AuthFacebookFragment(onFacebookAuthResponse())
            val arguments = Bundle()
            arguments.putBoolean("isLogin", mIsLogin)
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(android.R.id.content, fragment, FACEBOOK_FRAGMENT_TAG).commit()
        }
    }

    private fun onFacebookAuthResponse(): OnAuthResponseCallback {
        return object : OnAuthResponseCallback {
            override fun onResponseRegister(intent: Intent) {
                removeAuthFragment()
                register(intent)
            }
            override fun onResponseLogin(intent: Intent, token: String?, expire: Long) {
                ContextHelper.setFirebaseJwtToken(this@LoginActivity, token)
                removeAuthFragment()
                navigateToMain(intent)
            }
            override fun onCancel() {
                removeAuthFragment()
            }
            override fun onError(error: Exception) {
                removeAuthFragment()
                AuthExceptionHandler.getInstance(this@LoginActivity)
                    .showError(login_container, TAG, error)
            }
        }
    }

    private fun removeAuthFragment() {
        progress_bar.visibility = View.INVISIBLE
        supportFragmentManager.popBackStack()
    }

    private fun handleGoogleAuth() {
        btn_login_or_register_google.setOnClickListener {
            val fragment = AuthGoogleFragment(onGoogleAuthResponse())
            val arguments = Bundle()
            arguments.putBoolean("isLogin", mIsLogin)
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(android.R.id.content, fragment, GOOGLE_FRAGMENT_TAG).commit()
        }
    }

    private fun onGoogleAuthResponse(): OnAuthResponseCallback {
        return object : OnAuthResponseCallback {
            override fun onResponseRegister(intent: Intent) {
                removeAuthFragment()
                register(intent)
            }
            override fun onResponseLogin(intent: Intent, jwt: String?, expire: Long) {
                ContextHelper.setFirebaseJwtToken(this@LoginActivity, jwt)
                removeAuthFragment()
                navigateToMain(intent)
            }
            override fun onCancel() {
                removeAuthFragment()
            }
            override fun onError(error: Exception) {
                removeAuthFragment()
                AuthExceptionHandler.getInstance(this@LoginActivity)
                    .showError(login_container, TAG, error)
            }
        }
    }


    private fun tryLogin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            AuthExceptionHandler.getInstance(this@LoginActivity)
                .showError(login_container, TAG, LoginEmptyFieldsException())
        } else {
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        progress_bar.visibility = View.VISIBLE
        btn_login_or_register.isEnabled = false
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mUser = FirebaseAuth.getInstance().currentUser
                    mUser!!.getIdToken(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                ContextHelper.setFirebaseJwtToken(this, task.result!!.token)
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("userId", mUser.uid)//task.result?.user!!.uid)
                                finish()
                                startActivity(intent)
                            } else {
                                AuthExceptionHandler.getInstance(this@LoginActivity)
                                    .showError(login_container, TAG, LoginFireBaseAuthException())
                            }
                        }
                } else {
                    AuthExceptionHandler.getInstance(this@LoginActivity)
                        .showError(login_container, TAG, LoginFireBaseAuthException())
                }
                progress_bar.visibility = View.INVISIBLE
                btn_login_or_register.isEnabled = true
            }
    }

    private fun tryRegister(email: String, password: String) {
        if (email.isBlank()) {
            AuthExceptionHandler.getInstance(this)
                .showError(login_container, TAG, LoginEmptyFieldsException())
        } else {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("password", password)
            intent.putExtra("provider", AuthProvider.EMAIL)
            register(intent)
        }
    }

    private fun register(intent: Intent) {
        progress_bar.visibility = View.VISIBLE
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(intent.getStringExtra("email"), "empty")
            .addOnCompleteListener { task ->
                when (task.exception) {
                    is FirebaseAuthInvalidUserException -> {
                        startActivity(intent)
                    }
                    else -> {
                       AuthExceptionHandler.getInstance(this)
                           .showError(login_container, TAG, task.exception!!)
                    }
                }
                progress_bar.visibility = View.INVISIBLE
            }
    }


    private fun handleLogoVisibility() {
        KeyboardVisibilityEvent.setEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        iv_login_logo.visibility = View.GONE
                    } else {
                        iv_login_logo.visibility = View.VISIBLE
                    }
                }
            }
        )
    }

    private fun handleLoginOrRegistration() {
        iv_login_logo.setOnClickListener { iv_login_logo.startAnimation(FlipAnimation()) }
        txt_login_or_register.setOnClickListener {
            mIsLogin = !mIsLogin
            updateUI()
        }
        btn_login_or_register.setOnClickListener {
            val email = etxt_login_email.text.toString()
            val password = etxt_login_password.text.toString()
            if (!mIsLogin) {
                tryRegister(email, password)
            }
            else tryLogin(email, password)
        }
    }

    private fun updateUI() {
        if (iv_login_logo.visibility == View.VISIBLE)
            iv_login_logo.startAnimation(FlipAnimation())
        txt_login_title.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.anim_fade_out))
        txt_login_title.postDelayed({
            updateFields()
            txt_login_title.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.anim_fade_in))

        }, ANIMATION_DELAY_100)
    }

    private fun updateFields() {
        if (!mIsLogin) {
            txt_login_or_register_social.text = getString(R.string.login_label_register_social)
            btn_login_or_register.text = getString(R.string.btn_sign_up_title)
            showWelcomeText()
            hidePasswordField()
        } else {
            txt_login_or_register_social.text = getString(R.string.login_label_login_social)
            btn_login_or_register.text = getString(R.string.btn_login_title)
            showPasswordField()
            showLoginText()
        }
    }

    private fun showWelcomeText() {
        txt_login_title.text = getText(R.string.login_label_welcome)
        txt_login_or_register.text = getText(R.string.login_label_login_now)
    }

    private fun showLoginText() {
        txt_login_or_register.text = getText(R.string.login_label_register_now)
        txt_login_title.text = getText(R.string.login_label)
    }

    private fun showPasswordField() {
        ll_btn_continue.postDelayed({
            etxt_login_password.visibility = View.VISIBLE
            etxt_login_password.alpha = 0f
            etxt_login_password.animate().alpha(1f).duration = ANIMATION_DELAY_200
        }, ANIMATION_DELAY_100)
    }

    private fun hidePasswordField() {
        etxt_login_password.animate().alpha(0.0f)
        etxt_login_password.visibility = View.GONE
    }


    private fun navigateToMain(intent: Intent) {
        startActivity(intent)
        finish()
    }

    private companion object {
        private var mIsLogin = false
        private const val TAG = "LoginActivity"
        private const val FACEBOOK_FRAGMENT_TAG = "FB_FRAGMENT"
        private const val GOOGLE_FRAGMENT_TAG = "GOOGLE_FRAGMENT"
        private const val ANIMATION_DELAY_200 = 200L
        private const val ANIMATION_DELAY_100 = 200L
    }
}
