package com.piso12.indoorex.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.fragments.category.InterestsGridFragment
import com.piso12.indoorex.fragments.category.OnUpdateInterestsListener
import com.piso12.indoorex.interactors.CategoryInteractor
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.viewmodels.CategoryViewModel
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.user_detail_interests_edit.*

class EditInterestsFragment : Fragment() {

    private lateinit var mCategoryViewModel: CategoryViewModel
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mSelectedCategories: List<Category>
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_detail_interests_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        CategoryInteractor.initialize(activity!!, edit_interests_container)
        CategoryInteractor.getCategories()
        handleUpdateInterests()
    }

    private fun handleUpdateInterests() {
        btn_user_interests_edit_done.setOnClickListener {
            CategoryInteractor.postCategories(mSelectedCategories)
            fragmentManager?.popBackStack()
        }
    }

    private fun loadInterestsFragment(categories: List<Category>) {
        val selectedCategories = mCategoryViewModel.mSelectedCategories.value!!.toMutableList()
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.user_interests_edit_container,
                InterestsGridFragment(categories, selectedCategories, onUpdateInterestsListener()))
            ?.commit()
    }

    private fun onUpdateInterestsListener() = object : OnUpdateInterestsListener {
        override fun onUpdate(categories: List<Category>) {
            mSelectedCategories = categories
        }
    }

    private fun initializeObservers() {
        activity?.let { 
            mCategoryViewModel = ViewModelProviders.of(it).get(CategoryViewModel::class.java)
            mUserViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java)
        }
        mCategoryViewModel.mCategories.observe(viewLifecycleOwner, Observer { categories ->
            if (categories.isNotEmpty()) {
                loadInterestsFragment(categories)
            }
        })
    }

    interface OnEditInterestsListener {
        fun onClose()
    }
}