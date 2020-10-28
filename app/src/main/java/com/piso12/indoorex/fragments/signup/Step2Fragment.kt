package com.piso12.indoorex.fragments.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.sign_up_step_2_fragment.*

class Step2Fragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mUserViewModel = activity.run {
            ViewModelProviders.of(this!!).get(UserViewModel::class.java)
        }
        return inflater.inflate(R.layout.sign_up_step_2_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleName()
        handleLastName()
    }

    private fun handleName() {
        etxt_sign_up_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // We do nothing after text is changed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We do nothing before text is changed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val user = mUserViewModel.mDummyUser.value!!
                user.name = s.toString()
                mUserViewModel.mDummyUser.postValue(user)
            }
        })
    }

    private fun handleLastName() {
        etxt_sign_up_last_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // We do nothing after text is changed
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We do nothing before text is changed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val user = mUserViewModel.mDummyUser.value!!
                user.lastName = s.toString()
                mUserViewModel.mDummyUser.postValue(user)
            }
        })
    }
}
