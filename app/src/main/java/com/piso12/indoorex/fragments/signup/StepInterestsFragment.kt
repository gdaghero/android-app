package com.piso12.indoorex.fragments.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.activities.MainActivity
import com.piso12.indoorex.fragments.category.InterestsGridFragment
import com.piso12.indoorex.fragments.category.OnUpdateInterestsListener
import com.piso12.indoorex.interactors.CategoryInteractor
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.viewmodels.CategoryViewModel
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.sign_up_interests.*

class StepInterestsFragment : Fragment() {

    private lateinit var mSelectedCategories: List<Category>
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mCategoryViewModel: CategoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              state: Bundle?): View? {
        return inflater.inflate(R.layout.sign_up_interests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        fetchInterests()
        handleContinue()
    }

    private fun initializeObservers() {
        activity?.let {
            mUserViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java)
            mCategoryViewModel = ViewModelProviders.of(it).get(CategoryViewModel::class.java)
        }
        mUserViewModel.mDummyUser.observe(viewLifecycleOwner, Observer { user ->
            txt_sign_up_interests_username.text = user.name
        })
        mCategoryViewModel.mCategories.observe(viewLifecycleOwner, Observer { categories ->
            loadInterests(categories)
        })
    }

    private fun fetchInterests() {
        CategoryInteractor.initialize(activity!!, sign_up_interests_container)
        CategoryInteractor.getCategories()
    }

    private fun loadInterests(categories: List<Category>) {
        mSelectedCategories = listOf()
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.sign_up_interests_grid_container,
                InterestsGridFragment(categories, mutableListOf(), onUpdateInterestsListener()))
            ?.commit()
    }

    private fun onUpdateInterestsListener() = object : OnUpdateInterestsListener {
        override fun onUpdate(categories: List<Category>) {
            mSelectedCategories = categories
        }
    }

    private fun handleContinue() {
        btn_sign_up_done.setOnClickListener {
            if (mSelectedCategories.isNotEmpty()) {
                CategoryInteractor.postCategories(mSelectedCategories)
            }
            activity?.finish()
            startActivity(Intent(activity, MainActivity::class.java))
        }
    }
}