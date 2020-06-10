package com.exercises.textgame.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.exercises.textgame.R
import kotlinx.android.synthetic.main.layout_loading_dialog.*

class AlertDialogFragment: DialogFragment() {
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
        val alertText = rootView.findViewById<TextView>(R.id.tvDialog)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        lavLoading.setMinAndMaxFrame(0,120)
        lavLoading.playAnimation()
        val width  = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

}