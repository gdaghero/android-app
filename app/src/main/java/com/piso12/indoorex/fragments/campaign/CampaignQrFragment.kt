package com.piso12.indoorex.fragments.campaign

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.piso12.indoorex.R
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.exceptions.campaign.CampaignExceptionHandler
import com.piso12.indoorex.exceptions.campaign.CreateCampaignQrCodeException
import com.piso12.indoorex.tasks.CreateQrCodeTask
import kotlinx.android.synthetic.main.campaign_detail_qr.*

class CampaignQrFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.campaign_detail_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleBackPress(view)
        handleClose()
        handleCampaign()
    }

    private fun handleClose() {
        iv_qr_close.setOnClickListener {
            onClose()
        }
    }

    private fun handleBackPress(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onClose()
                true
            } else {
                false
            }
        }
    }

    private fun onClose() {
        fragmentManager?.let {
            it.findFragmentByTag(this::class.simpleName)?.let { fr ->
                it.beginTransaction().remove(fr).commitNow()
            }
        }
    }

    private fun handleCampaign() {
        val campaign = arguments?.getSerializable("campaign") as CampaignDto
        Glide
            .with(this)
            .load(campaign.place?.mediaResource!!.url)
            .circleCrop()
            .into(iv_feed_store_logo)
        tv_campaign_detail_qr_title.text = campaign.name
        tv_campaign_detail_qr_store_name.text = campaign.place?.name
        CreateQrCodeTask(onCreateQrCodeTaskComplete(), activity!!).execute(campaign)
    }

    private fun onCreateQrCodeTaskComplete(): CreateQrCodeTaskCallback {
        return object : CreateQrCodeTaskCallback {
            override fun onSuccess(bitmap: Bitmap) {
                iv_campaign_qr.setImageBitmap(bitmap)
            }

            override fun onError(ex: Exception) {
                CampaignExceptionHandler.getInstance(activity!!)
                    .showError(campaign_qr_container, TAG, CreateCampaignQrCodeException())
            }
        }
    }

    interface CreateQrCodeTaskCallback {
        fun onSuccess(bitmap: Bitmap)
        fun onError(ex: Exception)
    }

    private companion object {
        private const val TAG = "QrFragment"
    }
}
