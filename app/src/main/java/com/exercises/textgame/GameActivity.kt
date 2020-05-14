package com.exercises.textgame

import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        button.setOnClickListener {
            button.setOnClickListener {
                val animMoveRight = AnimationUtils.loadAnimation(this,R.anim.move_right)
                textView3.startAnimation(animMoveRight)
            }
        }
    }
}
