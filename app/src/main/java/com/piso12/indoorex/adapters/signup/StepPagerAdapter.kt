package com.piso12.indoorex.adapters.signup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.piso12.indoorex.fragments.signup.Step1Fragment
import com.piso12.indoorex.fragments.signup.Step2Fragment
import com.piso12.indoorex.fragments.signup.Step3Fragment
import com.piso12.indoorex.fragments.signup.Step4Fragment
import com.piso12.indoorex.fragments.signup.Step5Fragment

class StepPagerAdapter(fragmentManager: FragmentManager)
    : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            STEP_1 -> Step1Fragment()
            STEP_2 -> Step2Fragment()
            STEP_3 -> Step3Fragment()
            STEP_4 -> Step4Fragment()
            STEP_5 -> Step5Fragment()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getCount(): Int {
        return MAX_STEPS
    }

    private companion object {
        private const val MAX_STEPS = 5
        private const val STEP_1 = 0
        private const val STEP_2 = 1
        private const val STEP_3 = 2
        private const val STEP_4 = 3
        private const val STEP_5 = 4
    }
}
