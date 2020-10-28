package com.piso12.indoorex.fragments.search.stores

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.piso12.indoorex.R
import com.piso12.indoorex.interactors.PlaceInteractor
import com.piso12.indoorex.models.Plan
import com.piso12.indoorex.viewmodels.PlaceViewModel
import kotlinx.android.synthetic.main.search_store_detail_map.*


class StoreDetailMapFragment : Fragment() {

    private lateinit var mPlaceViewModel: PlaceViewModel
    private lateinit var mBottomSheet: BottomSheetBehavior<RelativeLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_store_detail_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeInteractors()
        initializeObservers()
        handleBottomSheetPanel()
        fetchPlan()
        handleCloseMap()
    }

    private fun handleCloseMap() {
        iv_store_map.setOnTouchListener { view, event ->
            if (view.width - event.x <= 100 && event.y <= 100) {
                closeMap()
                true
            }
            false
        }
    }

    private fun closeMap() {
        fragmentManager?.let {
            it.popBackStack()
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mPlaceViewModel = ViewModelProviders.of(it).get(PlaceViewModel::class.java)
        }
        mPlaceViewModel.mPlan.observe(viewLifecycleOwner, Observer { plan ->
            plan?.let {
                loadPlan(it)
            }
        })
    }

    private fun initializeInteractors() {
        activity?.let {
            PlaceInteractor.initialize(it)
        }
    }

    private fun fetchPlan() {
        arguments?.let {
            map_progress.visibility = View.VISIBLE
            PlaceInteractor.getPlan(it.getInt("planId"))
        }
    }

    private fun loadPlan(plan: Plan) {
        showPin()
        Glide.with(this)
            .asBitmap()
            .load(plan.mImageUrl)
            .into(object : CustomViewTarget<MapView, Bitmap>(iv_store_map) {

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // Nothing to do here
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    // Nothing to do here
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    iv_store_map.setImage(ImageSource.cachedBitmap(resource))
                    map_progress.visibility = View.GONE
                    ib_close_map.visibility = View.VISIBLE
                }
            })
    }

    private fun showPin() {
        arguments?.let {
            iv_store_map.add(PointF(
                it.getInt("x").toFloat(),
                it.getInt("y").toFloat()
            ))
        }
    }

    private fun handleBottomSheetPanel() {
        mBottomSheet = BottomSheetBehavior.from(bs_store_map as RelativeLayout)
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onPause() {
        super.onPause()
        PlaceInteractor.removeCallbacks()
    }
}
