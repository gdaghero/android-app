package com.piso12.indoorex.fragments.feed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.piso12.indoorex.fragments.campaign.CampaignOptionsFragment

class BackgroundFragment(color: String) : Fragment() {

    private lateinit var mBackground: View
    private val mColor = color

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBackground = View(context)
        mBackground.setBackgroundColor(Color.parseColor(mColor))
        mBackground.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        return mBackground
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBackground.setOnClickListener {
            fragmentManager?.let {
                it.findFragmentByTag(this::class.simpleName)?.let { bg ->
                    it.beginTransaction().remove(bg).commitNow()
                }
                it.findFragmentByTag(CampaignOptionsFragment::class.simpleName)?.let { fr ->
                    it.beginTransaction().remove(fr).commitNow()
                }
            }
        }
    }

    fun onClosing(alpha: Number) {
        mBackground.alpha = alpha.toFloat()
    }
}