package com.exercises.textgame

import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import android.content.Intent
import android.util.Log
import android.view.animation.Animation
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val animSplash = AnimationUtils.loadAnimation(this,R.anim.splash)

//        fetchUsers()
        logo_splash.startAnimation(animSplash)

        animSplash.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                logo_splash.visibility = View.INVISIBLE
                //Log.d(SplashActivity::class.java.simpleName,"*******************$currentUser")
                if (Firebase.auth.currentUser != null) {
                    //val intent = Intent(this@SplashActivity, LobbyActivity::class.java)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
                else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
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
