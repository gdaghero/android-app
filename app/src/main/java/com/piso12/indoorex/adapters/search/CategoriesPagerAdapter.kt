package com.piso12.indoorex.adapters.search

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class CategoriesPagerAdapter(group: List<View>) : PagerAdapter() {

    private val mGroups = group

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return mGroups.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = mGroups[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(mGroups[position])
    }
}
