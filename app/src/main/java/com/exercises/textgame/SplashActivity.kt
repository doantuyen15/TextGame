package com.exercises.textgame

import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import android.content.Intent
import android.view.animation.Animation
import android.view.View
import com.google.firebase.auth.FirebaseUser


class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var signedUser : FirebaseUser? = auth.currentUser
        val animSplash = AnimationUtils.loadAnimation(this,R.anim.splash)
        logo_splash.startAnimation(animSplash)

        animSplash.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                logo_splash.visibility = View.INVISIBLE
                if (signedUser != null) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                else {
                    startActivity(Intent(this@SplashActivity, SignupActivity::class.java))
                }
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })


    }

}
