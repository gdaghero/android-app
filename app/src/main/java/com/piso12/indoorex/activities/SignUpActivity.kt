package com.piso12.indoorex.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.piso12.indoorex.R
import com.piso12.indoorex.adapters.signup.StepPagerAdapter
import com.piso12.indoorex.adapters.signup.StepPagerSocialAdapter
import com.piso12.indoorex.animations.DepthPageTransformer
import com.piso12.indoorex.dtos.UserDto
import com.piso12.indoorex.exceptions.auth.AuthExceptionHandler
import com.piso12.indoorex.exceptions.auth.LoginFireBaseAuthException
import com.piso12.indoorex.fragments.auth.AuthProvider
import com.piso12.indoorex.fragments.signup.StepInterestsFragment
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.interactors.UserInteractor
import com.piso12.indoorex.services.user.UserService
import com.piso12.indoorex.services.utils.ServiceCallback
import com.piso12.indoorex.services.utils.ServiceResult
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.sign_up_activity.*
import kotlinx.android.synthetic.main.sign_up_progress_bar.*
import kotlinx.android.synthetic.main.util_progress_bar.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)
        initializeSteps()
        initializeObserver()
        handleStepBack()
        handleStepNext()
        handleSignUp()
    }

    private fun handleStepBack() {
        btn_sign_up_go_back.setOnClickListener {
            steps_pager.currentItem--
            handlePageSelection(steps_pager.currentItem)
            handleNextEnabled()
        }
    }

    private fun handleStepNext() {
        btn_sign_up_continue.setOnClickListener {
            steps_pager.currentItem++
            handlePageSelection(steps_pager.currentItem)
            handleNextEnabled()
        }
    }

    private fun handlePageSelection(position: Int) {
        when (position) {
            0 -> btn_sign_up_go_back.visibility = View.GONE
            sign_up_progress_bar.max - 1 -> {
                btn_sign_up_continue.visibility = View.GONE
                btn_sign_up_done.visibility = View.VISIBLE
            }
            else -> {
                btn_sign_up_done.visibility = View.GONE
                btn_sign_up_go_back.visibility = View.VISIBLE
                btn_sign_up_continue.visibility = View.VISIBLE
            }
        }
        sign_up_progress_bar.progress = position + 1
    }

    private fun initializeObserver() {
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        mUserViewModel.mPassword.observe(this, Observer { mPassword = it })
        mUserViewModel.mPasswordConfirmation.observe(this, Observer { mPasswordConfirmation = it })
        mUserViewModel.mDummyUser.observe(this, Observer {
            handleNextEnabled()
        })
    }

    private fun handleNextEnabled() {
        val provider = intent.getSerializableExtra("provider")
        mUserViewModel.mDummyUser.value?.let { user ->
            if (provider == AuthProvider.EMAIL) {
                handleNextEnabledEmail(user)
            } else {
                handleNextEnabledSocial(user)
            }
        }
    }

    private fun handleNextEnabledSocial(user: UserDto) {
        var isEnabled = false
        when (steps_pager.currentItem) {
            0 -> isEnabled = user.phoneNumber.isNotBlank()
            1 -> isEnabled = true
            2 -> btn_sign_up_done.isEnabled = user.gender.isNotBlank()
        }
        btn_sign_up_continue.isEnabled = isEnabled
    }

    private fun handleNextEnabledEmail(user: UserDto) {
        var isEnabled = false
        when (steps_pager.currentItem) {
            0 -> isEnabled = user.phoneNumber.isNotBlank()
            1 -> isEnabled = user.name.isNotBlank() && user.lastName.isNotBlank()
            2 -> isEnabled = true
            3 -> isEnabled = user.gender.isNotBlank()
            4 -> btn_sign_up_done.isEnabled = mPassword.isNotBlank() && mPasswordConfirmation.isNotBlank()
        }
        btn_sign_up_continue.isEnabled = isEnabled
    }

    private fun initializeSteps() {
        if (intent.getSerializableExtra("provider") == AuthProvider.EMAIL) {
            setupStepPagerAdapter()
        } else setupStepPagerSocialAdapter()
        steps_pager.addOnPageChangeListener(onPageChange())
        steps_pager.setPageTransformer(true,
            DepthPageTransformer()
        )
    }

    private fun onPageChange(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // We do nothing when page scroll state is changed
            }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                // We do nothing when page has scrolled
            }
            override fun onPageSelected(position: Int) {
                handlePageSelection(position)
            }
        }
    }

    private fun handleSignUp() {
        btn_sign_up_done.setOnClickListener {
            sign_up_progress_bar.visibility = View.GONE
            progress_bar.visibility = View.VISIBLE
            when (intent.getSerializableExtra("provider")) {
                AuthProvider.FACEBOOK -> signUpWithFacebook()
                AuthProvider.GOOGLE -> signUpWithGoogle()
                AuthProvider.EMAIL -> signUpWithEmailAndPassword()
            }
        }
    }

    private fun signUpWithFacebook() {
        mUserViewModel.mDummyUser.value?.let {
            it.email = intent.getStringExtra("email")
            it.name = intent.getStringExtra("name")
            it.lastName = intent.getStringExtra("lastName")
            val credential = FacebookAuthProvider
                .getCredential(intent.getStringExtra("token"))
            signInWithCredential(credential)
        }
    }

    private fun signUpWithGoogle() {
        mUserViewModel.mDummyUser.value?.let {
            it.email = intent.getStringExtra("email")
            it.name = intent.getStringExtra("name")
            it.lastName = intent.getStringExtra("lastName")
            val credential = GoogleAuthProvider
                .getCredential(intent.getStringExtra("token"), null)
            signInWithCredential(credential)
        }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    registerUserInBackofficeApi()
                } else {
                    AuthExceptionHandler.getInstance(this@SignUpActivity)
                        .showError(sign_up_container, TAG, LoginFireBaseAuthException())
                    progress_bar.visibility = View.GONE
                    sign_up_progress_bar.visibility = View.VISIBLE
                }
            }
    }

    private fun signUpWithEmailAndPassword() {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(mUserViewModel.mDummyUser.value!!.email, mPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    registerUserInBackofficeApi()
                } else {
                    AuthExceptionHandler.getInstance(this@SignUpActivity)
                        .showError(sign_up_container, TAG, task.exception!!)
                    progress_bar.visibility = View.GONE
                    sign_up_progress_bar.visibility = View.VISIBLE
                }
            }
    }

    private fun arePasswordsValid(): Boolean {
        if (mPassword.isBlank() || mPasswordConfirmation.isBlank()) {
            Snackbar.make(sign_up_container, "Debe ingresar una contraseña", Snackbar.LENGTH_LONG)
                .show()
            return false
        }
        if (mPassword != mPasswordConfirmation) {
            Snackbar.make(sign_up_container, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    private fun registerUserInBackofficeApi() {
        val provider = intent.getSerializableExtra("provider")
        if (provider == AuthProvider.EMAIL && !arePasswordsValid()) {
            progress_bar.visibility = View.GONE
            sign_up_progress_bar.visibility = View.VISIBLE
            return
        }
        mUserViewModel.mDummyUser.value?.let {
            it.id = FirebaseAuth.getInstance().currentUser?.uid!!
            it.fcmToken = ContextHelper.getFirebaseToken(this)
            UserService(this).postUser(it, onRegisterUserInBackofficeResponse())
        }
    }

    private fun onRegisterUserInBackofficeResponse(): ServiceCallback<String> {
        return object : ServiceCallback<String> {
            override fun onComplete(result: ServiceResult<String>) {
                if (result.isSuccessful) {
                    fetchUser()
                    showInterestsSection()
                } else {
                    AuthExceptionHandler.getInstance(this@SignUpActivity)
                        .showError(sign_up_container, TAG, result.error!!)
                    FirebaseAuth.getInstance().currentUser?.delete()
                }
                progress_bar.visibility = View.GONE
                sign_up_progress_bar.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
        UserInteractor.initialize(this, sign_up_container)
        UserInteractor.fetchUser(userId)
    }

    private fun showInterestsSection() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.sign_up_container, StepInterestsFragment())
            .commit()
    }

    private fun setupStepPagerAdapter() {
        val adapter = StepPagerAdapter(supportFragmentManager)
        steps_pager.adapter = adapter
        sign_up_progress_bar.max = adapter.count
    }

    private fun setupStepPagerSocialAdapter() {
        val adapter = StepPagerSocialAdapter(supportFragmentManager)
        steps_pager.adapter = adapter
        sign_up_progress_bar.max = adapter.count
    }

    private companion object UserModel {
        private const val TAG = "SignUpActivity"
        private lateinit var mUserViewModel: UserViewModel
        private lateinit var mPassword: String
        private lateinit var mPasswordConfirmation: String
    }
}
