package com.exercises.textgame.fragment

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.fragment.app.DialogFragment
import com.exercises.textgame.R
import kotlinx.android.synthetic.main.layout_loading_dialog.*

class AlertDialogFragment(private val mListener: DetachDialogListener): DialogFragment() {
    private var firstTry = true
//    private lateinit var mListener: DetachDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.layout_loading_dialog, container, false)
        val buttonCancel = rootView.findViewById<Button>(R.id.btnDialogCancel)
        buttonCancel.setOnClickListener {
            dialog?.dismiss()
            mListener.onDetachDialog()
        }
        return rootView
    }

    override fun onStart() {
        super.onStart()
        firstTry = false
        lavLoading.setMinAndMaxFrame(0,120)
        lavLoading.playAnimation()
        val width  = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.setCancelable(false)
    }

    fun setState(isConnected: Boolean) {
        if (isConnected && dialog?.isShowing == true){
            dialog?.btnDialogCancel?.visibility = View.GONE
            lavLoading.setMinAndMaxFrame(240, 400)
            lavLoading.playAnimation()
            tvDialog.text = getString(R.string.reconnected)
            lavLoading.addAnimatorUpdateListener {valueAnimator ->
                val progress = (valueAnimator.animatedValue as Float * 100).toInt()
                // animation with 841 frame
                if(progress >= 47){
                    lavLoading.cancelAnimation()
                    if (dialog?.isShowing!!) {
                        dialog?.dismiss()
                    }
                }
            }
        } else if(!firstTry){
            dialog?.btnDialogCancel?.visibility = View.VISIBLE
            tvDialog?.text = getString(R.string.try_to_reconnect)
            lavLoading?.setMinAndMaxFrame(0, 120)
            lavLoading?.playAnimation()
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mListener = activity as DetachDialogListener
//    }

    interface DetachDialogListener {
        fun onDetachDialog()
    }
}