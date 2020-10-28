package com.piso12.indoorex.fragments.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.sign_up_step_1_fragment.*

class Step1Fragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              state: Bundle?): View? {
        mUserViewModel = activity.run {
            ViewModelProviders.of(this!!).get(UserViewModel::class.java)
        }
        return inflater.inflate(R.layout.sign_up_step_1_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        markEmailAsValid()
        handlePhone()
        handleEmail()
    }

    private fun handlePhone() {
        val countryCode = ccp_sign_up_phone.selectedCountryCodeWithPlus
        etxt_sign_up_phone.requestFocus()
        etxt_sign_up_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // We do nothing after text is changed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We do nothing before text is changed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { phoneNumber ->
                    if (count <= 15) {
                        val user = mUserViewModel.mDummyUser.value
                        if (phoneNumber.isEmpty()) {
                            user!!.phoneNumber = ""
                        } else {
                            user!!.phoneNumber = "$countryCode${phoneNumber}"
                        }
                        mUserViewModel.mDummyUser.postValue(user)
                    }
                    }
                }
        })
    }

    override fun onStop() {
        super.onStop()
        ContextHelper.hideSoftKeyboard(activity!!, etxt_sign_up_phone)
    }

    private fun handleEmail() {
        val user = mUserViewModel.mDummyUser.value
        user!!.email = activity!!.intent.getStringExtra("email")
        etxt_sign_up_email.setText(user.email)
        mUserViewModel.mDummyUser.postValue(user)
    }

    private fun markEmailAsValid() {
        val drawable = getDrawable(context!!, R.drawable.ic_done_24px)!!
        drawable.setTint(Color.parseColor("#FF26C78E"))
        etxt_sign_up_email.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }
}
