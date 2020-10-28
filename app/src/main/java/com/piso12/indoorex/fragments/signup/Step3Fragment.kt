package com.piso12.indoorex.fragments.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.viewmodels.UserViewModel
import com.piso12.indoorex.helpers.ContextHelper
import kotlinx.android.synthetic.main.sign_up_step_3_fragment.*
import java.time.LocalDateTime
import java.util.Date

class Step3Fragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mUserViewModel = activity.run {
            ViewModelProviders.of(this!!).get(UserViewModel::class.java)
        }
        return inflater.inflate(R.layout.sign_up_step_3_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dp_sign_up_birth_date.maxDate = Date().time
        handleDateSelection()
    }

    override fun onResume() {
        super.onResume()
        dp_sign_up_birth_date.requestFocus()
        ContextHelper.hideSoftKeyboard(activity!!, dp_sign_up_birth_date)
    }

    private fun handleDateSelection() {
        val user = mUserViewModel.mDummyUser.value
        user!!.birthDate = LocalDateTime.now().toString()
        dp_sign_up_birth_date.setOnDateChangedListener { _, year, month, day ->
            user!!.birthDate = LocalDateTime.of(year, month, day, 0, 0).toString()
            mUserViewModel.mDummyUser.postValue(user)
        }
    }
}
