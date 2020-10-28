package com.piso12.indoorex.fragments.notification

import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import com.piso12.indoorex.R
import com.piso12.indoorex.interactors.NotificationInteractor
import com.piso12.indoorex.models.Notification
import kotlinx.android.synthetic.main.notification_popup_fragment.*

class NotificationPopupFragment : Fragment() {

    private lateinit var mCountdownTimer: CountDownTimer
    private lateinit var mNotification: Notification

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              state: Bundle?): View? {
        return inflater.inflate(R.layout.notification_popup_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupSlideUpGesture()
        setupClickListener()
        loadNotification()
        startTimer()
    }

    private fun setupClickListener() {
        notification_popup_container.setOnClickListener {
            mCountdownTimer.cancel()
            NotificationInteractor.openNotification(mNotification)
            hideNotification()
        }
    }

    private fun setupSlideUpGesture() {
        SlideUpBuilder(view)
            .withStartState(SlideUp.State.SHOWED)
            .withStartGravity(Gravity.TOP)
            .withListeners(object : SlideUp.Listener.Visibility {
                override fun onVisibilityChanged(visibility: Int) {
                    if (visibility == View.GONE) {
                        mCountdownTimer.cancel()
                        mCountdownTimer.onFinish()
                    }
                }
            })
            .build()
    }

    private fun startTimer() {
        mCountdownTimer = object : CountDownTimer(6 * 1000L, 1000) {
            override fun onFinish() {
                hideNotification()
            }
            override fun onTick(millisUntilFinished: Long) {
                // Nothing needs to be done on each tick
            }
        }
        mCountdownTimer.start()
    }

    private fun vibrate() {
        (context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
            .vibrate(VibrationEffect.createOneShot(200, 200))
    }

    private fun playRingtone() {
        RingtoneManager
            .getRingtone(context!!, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .play()
    }


    private fun loadNotification() {
        mNotification = arguments?.getSerializable("notification") as Notification
        Glide.with(this)
            .load(mNotification.imageUrl)
            .circleCrop()
            .into(iv_notification_store_logo)
        tv_notification_title.text = mNotification.title
        tv_notification_body.text = mNotification.body
        vibrate()
        playRingtone()
    }

    private fun hideNotification() {
        fragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(R.anim.anim_slide_down_to_origin, R.anim.anim_slide_up_from_origin)
            ?.remove(this)
            ?.commit()
    }
}