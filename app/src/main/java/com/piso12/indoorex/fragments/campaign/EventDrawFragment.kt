package com.piso12.indoorex.fragments.campaign

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.piso12.indoorex.R
import com.piso12.indoorex.helpers.ContextHelper
import com.piso12.indoorex.interactors.campaign.CampaignInteractor
import com.piso12.indoorex.services.auth.AuthService
import com.piso12.indoorex.viewmodels.DrawViewModel
import kotlinx.android.synthetic.main.event_draw_fragment.*
import java.util.*

class EventDrawFragment : Fragment() {

    private lateinit var mDrawViewModel: DrawViewModel
    private lateinit var mCampaignInteractor: CampaignInteractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.event_draw_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObservers()
        initInteractors()
        handleDrawConfirmation()
        loadDraw()
    }

    private fun initInteractors() {
        mCampaignInteractor = CampaignInteractor(this)
    }

    private fun initObservers() {
        activity?.let {
            mDrawViewModel = ViewModelProviders.of(it).get(DrawViewModel::class.java)
        }
    }

    private fun handleDrawConfirmation() {
        val draw = mDrawViewModel.mDraw.value
        btn_draw_confirmation.setOnClickListener {
            draw?.let {
                mCampaignInteractor.postDraw(it.mId, onPostDrawCompletedListener())
            }
        }
    }

    private fun onPostDrawCompletedListener(): OnPostDrawCompletedListener {
        return object : OnPostDrawCompletedListener {
            override fun onSuccess() {
                ll_draw_confirmed.visibility = View.VISIBLE
                btn_draw_confirmation.visibility = View.GONE
            }

            override fun onError() {
                Toast.makeText(
                    this@EventDrawFragment.context,
                    "Ocurrió un error al inscribirte al sorteo",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDraw() {
        arguments?.let {
            val isParticipating = it.getBoolean("isParticipating")
            if (isParticipating) {
                ll_draw_confirmed.visibility = View.VISIBLE
                btn_draw_confirmation.visibility = View.GONE
            } else {
                ll_draw_confirmed.visibility = View.GONE
                btn_draw_confirmation.visibility = View.VISIBLE
            }
        }
        val draw = mDrawViewModel.mDraw.value!!
        draw.mWinnersIds?.let {
            if (it == AuthService.getCurrentUser().uid) {
                val drawable = activity!!.getDrawable(R.drawable.ic_done_all_24px)
                drawable.setTint(Color.parseColor("#1D976C"))
                iv_draw_status.setImageDrawable(drawable)
                tv_draw_status.text = "Felicitaciones, ganaste este sorteo!"
            }
        }
        tv_draw_description.text = draw.mDescription
        val drawDate = Calendar.getInstance()
        drawDate.time = draw.mDate
        tv_draw_date.text = "${ContextHelper.getDayName(drawDate.get(Calendar.DAY_OF_WEEK))}, " +
                "${drawDate.get(Calendar.DAY_OF_MONTH)} de" +
                " ${ContextHelper.getMonthName(drawDate.get(Calendar.MONTH))}"
        tv_draw_time.text = "${drawDate.get(Calendar.HOUR_OF_DAY)}" +
                ":${drawDate.get(Calendar.MINUTE)}"
    }

    interface OnPostDrawCompletedListener {
        fun onSuccess()
        fun onError()
    }
}