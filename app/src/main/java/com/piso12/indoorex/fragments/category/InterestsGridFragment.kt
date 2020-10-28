package com.piso12.indoorex.fragments.category

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.piso12.indoorex.R
import com.piso12.indoorex.models.Category
import kotlinx.android.synthetic.main.interests_grid.*
import kotlinx.android.synthetic.main.interests_item.view.*

class InterestsGridFragment(categories: List<Category>, selectedCategories: MutableList<Category>,
                            callback: OnUpdateInterestsListener?) : Fragment() {

    private val mCallback = callback
    private val mCategories = categories
    private var mSelectedCategories = selectedCategories

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              state: Bundle?): View? {
        return inflater.inflate(R.layout.interests_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadInterests()
    }

    private fun loadInterests() {
        var index = 0
        mCategories.forEach { category ->
            val item = createInterestItem(index++, category)
            if (mSelectedCategories.contains(category)) {
                checkCategory(item)
            }
            interests_grid.addView(item)
        }
    }

    private fun createInterestItem(index: Int, category: Category): View {
        val container = LayoutInflater.from(context).inflate(R.layout.interests_item, null) as View
        loadImage(container, category.mImageUrl)
        container.tv_interest_name.text = category.mName
        container.layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED),
            GridLayout.spec(GridLayout.UNDEFINED, 1f))
        container.layoutParams.height = 400
        container.layoutParams.width = 0
        if (Math.floorMod(index, 2) == 0) {
            container.updatePadding(0, 0, 4, 4)
        } else {
            container.updatePadding(0, 0, 0, 4)
        }
        if (mCategories.size == index + 1) {
            container.updatePadding(0, 0, 0, 0)
        }
        mCallback?.let { callback ->
            container.interest_image.setOnClickListener {
                handleInterestClick(container, category)
                callback.onUpdate(mSelectedCategories)
            }
        }
        return container
    }

    private fun handleInterestClick(item: View, category: Category) {
        if (item.interest_check.visibility == View.VISIBLE) {
            uncheckCategory(item)
            mSelectedCategories.remove(category)
        } else {
            mSelectedCategories.add(category)
            checkCategory(item)
        }
    }

    private fun checkCategory(item: View) {
        item.interest_check.visibility = View.VISIBLE
        item.interest_image.alpha = 0.4f
        item.tv_interest_name.visibility = View.VISIBLE
    }

    private fun uncheckCategory(item: View) {
        item.interest_check.visibility = View.INVISIBLE
        item.interest_image.alpha = 1f
        item.tv_interest_name.visibility = View.INVISIBLE
    }

    private fun loadImage(view: View, imageUrl: String) {
        Glide
            .with(this)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    view.setBackgroundColor(Color.BLACK)
                    return false
                }
            })
            .into(view.interest_image)
    }
}