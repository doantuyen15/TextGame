package com.exercises.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import android.content.Intent
import android.view.animation.Animation
import android.widget.Toast
import android.view.View



class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val animSplash = AnimationUtils.loadAnimation(this,R.anim.splash)
        logo_splash.startAnimation(animSplash)
        animSplash.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onAnimationEnd(animation: Animation?) {
                logo_splash.setVisibility(View.GONE)
                startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            }

            override fun onAnimationStart(animation: Animation?) {
                // not implemented
            }

        })


    }

}
