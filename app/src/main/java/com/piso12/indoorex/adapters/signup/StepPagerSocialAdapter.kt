package com.piso12.indoorex.adapters.signup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.piso12.indoorex.fragments.signup.Step1Fragment
import com.piso12.indoorex.fragments.signup.Step3Fragment
import com.piso12.indoorex.fragments.signup.Step4Fragment

class StepPagerSocialAdapter(fragmentManager: FragmentManager)
    : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Step1Fragment()
            1 -> Step3Fragment()
            2 -> Step4Fragment()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}
