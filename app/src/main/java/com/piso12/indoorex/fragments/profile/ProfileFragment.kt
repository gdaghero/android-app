package com.piso12.indoorex.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.activities.LoginActivity
import com.piso12.indoorex.fragments.category.InterestsGridFragment
import com.piso12.indoorex.fragments.feed.BackgroundFragment
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.models.User
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.viewmodels.CategoryViewModel
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.user_detail_interests.*
import kotlinx.android.synthetic.main.user_fragment.*

class ProfileFragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mCategoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        handleLogOut()
        handleEditProfile()
        handleInterests()
    }

    private fun handleInterests() {
        iv_user_edit_interests.setOnClickListener {
            showInterestsFragment()
        }
    }

    private fun showInterestsFragment() {
        fragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom)
            ?.add(R.id.profile_container, EditInterestsFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun handleEditProfile() {
        btn_edit_profile.setOnClickListener {
            val profileEditFragment = ProfileEditFragment()
            val background = BackgroundFragment("#66000000")
            fragmentManager?.let {
                it.beginTransaction()
                .add(R.id.profile_container, background, BackgroundFragment::class.simpleName)
                .setCustomAnimations(R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom,
                    R.anim.anim_slide_bottom_top, R.anim.anim_slide_top_bottom)
                .add(R.id.profile_container, profileEditFragment)
                .addToBackStack(null)
                .commit()
            }
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mUserViewModel = ViewModelProviders.of(it).get(UserViewModel::class.java)
            mCategoryViewModel = ViewModelProviders.of(it).get(CategoryViewModel::class.java)
        }
        mUserViewModel.mUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let { loadUser(it) }
        })
        mCategoryViewModel.mSelectedCategories.observe(viewLifecycleOwner, Observer { categories ->
            loadInterests(categories)
        })
    }

    private fun loadInterests(interests: List<Category>) {
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.user_interests_container, InterestsGridFragment(interests,
                mutableListOf(), null), INTERESTS_GRID_FRAGMENT)
            ?.commit()
    }

    private fun loadUser(user: User) {
        txt_user_name.text = "${user.mName} ${user.mLastName}"
        txt_user_email.text = user.mEmail
        txt_user_phone.text = user.mPhoneNumber
        Glide
            .with(this)
            .load("https://pngimage.net/wp-content/uploads/2018/06/no-user-image-png-2.png")
            .into(img_user_profile)
        if (mCategoryViewModel.mSelectedCategories.value!!.isEmpty()) {
            mCategoryViewModel.mSelectedCategories.postValue(user.mCategories)
        }
    }

    private fun handleLogOut() {
        profile_log_out.setOnClickListener {
            AuthService.signOut()
            activity?.sendBroadcast(Intent("STOP_SERVICE_EVENT"))
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    }

    private companion object {
        private const val INTERESTS_GRID_FRAGMENT = "INTERESTS_GRID_FRAGMENT"
    }
}

