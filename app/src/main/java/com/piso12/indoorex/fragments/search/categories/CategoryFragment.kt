package com.piso12.indoorex.fragments.search.categories

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.adapters.search.CategoriesPagerAdapter
import com.piso12.indoorex.interactors.CategoryInteractor
import com.piso12.indoorex.models.Category
import com.piso12.indoorex.viewmodels.CategoryViewModel
import kotlinx.android.synthetic.main.search_categories.*
import kotlinx.android.synthetic.main.search_categories_item.view.*

class CategoryFragment : Fragment() {

    private lateinit var mCategoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeObservers()
        fetchCategories()
    }

    private fun fetchCategories() {
        mCategoryViewModel.mCategories.value?.let {
            if (it.isEmpty()) {
                CategoryInteractor.initialize(activity!!, search_categories)
                CategoryInteractor.getCategories()
            } else {
                loadCategories(it.toMutableList())
            }
        }
    }

    private fun initializeObservers() {
        activity?.let {
            mCategoryViewModel = ViewModelProviders.of(it).get(CategoryViewModel::class.java)
        }
        mCategoryViewModel.mCategories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                loadCategories(it.toMutableList())
            }
        })
    }

    private fun loadCategories(categories: MutableList<Category>) {
        val groups = mutableListOf<View>()
        val items = ROWS * COLUMNS
        mGroups = categories.size / items + categories.size % items - 1
        for (group in 0 until mGroups) {
            val groupView = createCategoryGroup()
            for (row in 0 until ROWS) {
                for (col in 0 until COLUMNS) {
                    var cat: Category? = null
                    if (categories.isNotEmpty()) {
                        cat = categories.removeAt(categories.size - 1)
                    }
                    val categoryView = createCategoryView(row, col, cat)
                    groupView.addView(categoryView)
                }
            }
            groups.add(groupView)
        }
        category_pager.adapter = CategoriesPagerAdapter(groups)
        category_pager.addOnPageChangeListener(onPageChange())
        addDots(0, mGroups)
    }

    private fun createCategoryGroup() : GridLayout {
        return LayoutInflater.from(context).inflate(R.layout.search_categories_group, null)
                as GridLayout
    }

    private fun createCategoryView(row: Int, col: Int, category: Category?): View {
        val item = LayoutInflater.from(context).inflate(
            R.layout.search_categories_item, null) as ViewGroup
        if (category != null) {
            Glide
                .with(this)
                .load(category.mImageUrl)
                .into(item.iv_category)
            item.tv_category_name.text = category.mName
            item.setOnClickListener { handleCategoryClick(category) }
        } else {
            item.category_container.cardElevation = 0f
            item.search_category_item_container.background =
                context!!.getDrawable(R.color.secondaryLightColor)
        }
        item.layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(row, 1f),
            GridLayout.spec(col, 1f))
        item.layoutParams.height = 0
        item.layoutParams.width = 0
        val params = item.layoutParams as (GridLayout.LayoutParams)
        params.updateMargins(0, 0, 32, 32)
        if (col == 2) {
            params.updateMargins(0, 0, 0, 32)
        }
        return item
    }

    private fun handleCategoryClick(category: Category) {
        val categoryDetail = CategoryDetailFragment()
        categoryDetail.arguments = Bundle().also { it.putSerializable("category", category) }
        fragmentManager?.let {
            it.beginTransaction()
                .setCustomAnimations(R.anim.anim_slide_from_left_to_origin, R.anim.anim_slide_from_right_to_origin)
                .add(R.id.search_coordinator, categoryDetail)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun onPageChange(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // We do nothing when scroll state is changed
            }
            override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
                // We do nothing when page is scrolled
            }
            override fun onPageSelected(position: Int) {
                category_pager_dots.removeAllViews()
                addDots(position, mGroups)
            }
        }
    }


    private fun addDots(selectedPageIndex: Int, numberOfPages: Int) {
        category_pager_dots.removeAllViews()
        for (page in 0 until numberOfPages) {
            val imageView = ImageView(context)
            if (selectedPageIndex == page) {
                imageView.setColorFilter(Color.GRAY)
                imageView.layoutParams = LinearLayout.LayoutParams(SELECTED_PAGE_DOT_SIZE,
                    SELECTED_PAGE_DOT_SIZE)
            } else {
                imageView.layoutParams = LinearLayout.LayoutParams(UNSELECTED_PAGE_DOT_SIZE,
                    UNSELECTED_PAGE_DOT_SIZE)
                imageView.setColorFilter(Color.LTGRAY)
            }
            imageView.setImageDrawable(context!!.getDrawable(R.drawable.ic_dot_24px))
            category_pager_dots.addView(imageView)
        }
    }

    private companion object {
        private const val ROWS = 2
        private const val COLUMNS = 3
        private const val SELECTED_PAGE_DOT_SIZE = 32
        private const val UNSELECTED_PAGE_DOT_SIZE = 24
        private var mGroups = 0
    }
}