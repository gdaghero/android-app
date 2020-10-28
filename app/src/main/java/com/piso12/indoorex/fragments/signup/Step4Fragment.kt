package com.piso12.indoorex.fragments.signup

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.sign_up_step_4_fragment.*

class Step4Fragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sign_up_step_4_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        loadImages()
        handleGenderSelection()
    }

    private fun initializeObservers() {
        activity?.let {
            mUserViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java)
        }
        mUserViewModel.mDummyUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                if (it.gender.isNotEmpty()) {
                    updateGender(it.gender)
                }
            }
        })
    }

    private fun loadImages() {
        Glide
            .with(this)
            .load("file:///android_asset/male.png")
            .circleCrop()
            .into(iv_sign_up_gender_male)
        Glide
            .with(this)
            .load("file:///android_asset/female.png")
            .circleCrop()
            .into(iv_sign_up_gender_female)
    }


    private fun handleGenderSelection() {
        val drawable = getDrawable(context!!, R.drawable.ic_done_24px)!!
        drawable.setTint(Color.parseColor("#FF26C78E"))
        iv_sign_up_gender_male.setOnClickListener {
            updateGender("M")
        }
        iv_sign_up_gender_female.setOnClickListener {
            updateGender("F")
        }
    }

    private fun updateGender(gender: String) {
        when (gender) {
            "M" -> {
                iv_male_check.visibility = View.VISIBLE
                iv_female_check.visibility = View.GONE
            }
            "F" -> {
                iv_female_check.visibility = View.VISIBLE
                iv_male_check.visibility = View.GONE
            }
            else -> {
                iv_female_check.visibility = View.GONE
                iv_male_check.visibility = View.GONE
            }
        }
        val user = mUserViewModel.mDummyUser.value
        user!!.gender = gender
        mUserViewModel.mDummyUser.postValue(user)
    }
}
