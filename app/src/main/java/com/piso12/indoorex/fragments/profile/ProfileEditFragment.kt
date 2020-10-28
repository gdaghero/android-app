package com.piso12.indoorex.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.UserDto
import com.piso12.indoorex.fragments.feed.BackgroundFragment
import com.piso12.indoorex.interactors.UserInteractor
import com.piso12.indoorex.models.User
import com.piso12.indoorex.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.user_detail_edit.*

class ProfileEditFragment : Fragment() {

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mBottomSheet: BottomSheetBehavior<RelativeLayout>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_detail_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleBottomSheetPanel()
        initializeObserver()
        loadUser()
        editUser()
    }

    private fun editUser() {
        user_edit_save.setOnClickListener {
            val user = UserDto()
            user.id = FirebaseAuth.getInstance().currentUser?.uid!!
            if (areFieldsValid()) {
                user.name = etxt_user_name.text.toString()
                user.lastName = etxt_user_last_name.text.toString()
                UserInteractor.editUser(user)
                removeFragments()
            } else {
                Snackbar.make(bs_edit_user, "Tenés que completar todos los campos!", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun areFieldsValid(): Boolean {
        return etxt_user_name.text.isNotBlank() && etxt_user_last_name.text.isNotBlank()
    }

    private fun initializeObserver() {
        mUserViewModel = ViewModelProviders.of(activity!!).get(UserViewModel::class.java)
    }

    private fun loadUser() {
        mUserViewModel.mUser.value?.let {
            etxt_user_name.setText(it.mName)
            etxt_user_last_name.setText(it.mLastName)
        }
        Glide
            .with(this)
            .load("https://pngimage.net/wp-content/uploads/2018/06/no-user-image-png-2.png")
            .into(img_edit_user_profile)
    }

    private fun handleBottomSheetPanel() {
        mBottomSheet = BottomSheetBehavior.from(bs_edit_user as RelativeLayout)
        mBottomSheet.skipCollapsed = true
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, offsetPx: Float) {
                // Nothing to do when sliding
            }
            override fun onStateChanged(view: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    removeFragments()
                }
            }
        })
    }

    private fun removeFragments() {
        fragmentManager?.let {
            val shadowFragment = it.findFragmentByTag(BackgroundFragment::class.java.simpleName)!!
            it.beginTransaction().remove(shadowFragment).commit()
            it.popBackStack()
        }
    }
}