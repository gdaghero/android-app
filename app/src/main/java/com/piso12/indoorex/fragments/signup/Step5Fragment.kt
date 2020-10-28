package com.piso12.indoorex.fragments.signup

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.sign_up_step_5_fragment.*

class Step5Fragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mUserViewModel = activity.run {
            ViewModelProviders.of(this!!).get(UserViewModel::class.java)
        }
        return inflater.inflate(R.layout.sign_up_step_5_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handlePasswordVisibility()
        handlePassword()
        handlePasswordConfirmation()
    }

    private fun handlePassword() {
        etxt_sign_up_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // We do nothing after text is changed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We do nothing before text is changed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mUserViewModel.mPassword.postValue(s.toString())
            }
        })
    }

    private fun handlePasswordConfirmation() {
        etxt_sign_up_password_confirmation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // We do nothing after text is changed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We do nothing before text is changed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mUserViewModel.mPasswordConfirmation.postValue(s.toString())
            }
        })
    }

    private fun handlePasswordVisibility() {
        handlePasswordVisibilityIconColor()
        var isPasswordVisible = false
        etxt_sign_up_password.setOnTouchListener { _, event ->
            var bounds = calculateBounds()
            if (bounds > 0 && event.action == MotionEvent.ACTION_UP && event.x >= bounds) {
                isPasswordVisible = !isPasswordVisible
                updatePasswordFieldVisibilityIcon(isPasswordVisible)
            }
            false
        }
    }

    private fun updatePasswordFieldVisibilityIcon(isVisible: Boolean) {
        var drawable = getDrawable(context!!, R.drawable.ic_visibility_off_24px)
        if (isVisible) {
            etxt_sign_up_password.transformationMethod = null
            etxt_sign_up_password_confirmation.transformationMethod = null
            etxt_sign_up_password
                .setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        } else {
            etxt_sign_up_password.transformationMethod = PasswordTransformationMethod()
            etxt_sign_up_password_confirmation.transformationMethod = PasswordTransformationMethod()
            etxt_sign_up_password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            drawable = getDrawable(context!!, R.drawable.ic_visibility_24px)
            etxt_sign_up_password
                .setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }
        updatePasswordFieldIconColor(etxt_sign_up_password.text, drawable!!)
    }

    private fun calculateBounds(): Int {
        return if (etxt_sign_up_password.compoundDrawables[2] != null) {
            etxt_sign_up_password.right - etxt_sign_up_password.paddingRight -
                    etxt_sign_up_password.compoundDrawables[2].bounds.width()
        } else 0
    }

    private fun handlePasswordVisibilityIconColor() {
        etxt_sign_up_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // We do nothing after text is changed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We do nothing before text is changed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePasswordFieldIconColor(s!!, etxt_sign_up_password.compoundDrawables[2])
            }
        })
    }

    private fun updatePasswordFieldIconColor(s: CharSequence, drawable: Drawable) {
        if (s.isNotEmpty()) {
            drawable.setTint(Color.parseColor("#212121"))
        } else {
            drawable.setTint(Color.parseColor("#808080"))
        }
    }

}
