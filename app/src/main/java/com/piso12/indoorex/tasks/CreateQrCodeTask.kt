package com.piso12.indoorex.tasks

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.AsyncTask
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.piso12.indoorex.dtos.CampaignDto
import com.piso12.indoorex.exceptions.campaign.CreateCampaignQrCodeException
import com.piso12.indoorex.fragments.campaign.CampaignQrFragment
import com.piso12.indoorex.services.auth.AuthService

class CreateQrCodeTask(callback: CampaignQrFragment.CreateQrCodeTaskCallback, context: Activity)
    : AsyncTask<CampaignDto, Void, Bitmap>() {

    private val mCallback = callback
    private val mContext = context

    override fun doInBackground(vararg params: CampaignDto?): Bitmap? {
        return createQrCode(params[0]!!)
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            mCallback.onSuccess(result)
        } else {
            mCallback.onError(CreateCampaignQrCodeException())
        }
    }

    private fun createQrCode(campaign: CampaignDto): Bitmap? {
        return try {
            val userId = AuthService.getCurrentUser().uid
            val data = "{ \"campaignId\": ${campaign.id}, \"userId\": $userId }"
            val qrgEncoder = QRGEncoder(data, QRGContents.Type.TEXT, getQrDimension())
            qrgEncoder.colorBlack = Color.parseColor("#FF1D976C")
            qrgEncoder.colorWhite = Color.parseColor("#FFFFFF")
            qrgEncoder.bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun getQrDimension(): Int {
        return getDisplaySize() - getDisplayDensity().times(QR_SIZE).toInt()
    }

    private fun getDisplaySize(): Int {
        val size = Point()
        mContext?.windowManager?.defaultDisplay?.getSize(size)
        return size.x
    }

    private fun getDisplayDensity(): Float {
        return mContext?.resources?.displayMetrics?.density!!
    }

    private companion object {
        private const val QR_SIZE = 100
        private const val TAG = "CreateQrCodeTask"
    }
}
