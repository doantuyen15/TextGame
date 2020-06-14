package com.exercises.textgame

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val bootCheck = checkGooglePlayService()
        if (!bootCheck) {
            showBootCheckDialog()
        } else {
            startBootAnimation()
        }
    }

    private fun showBootCheckDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setMessage("Up to date your Google Service first!")
            .setNegativeButton(
                "OK"
            ) { dialog, _ ->
                dialog?.dismiss()
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.gms")
                    )
                )
                finish()
            }
        val myDialog = builder.create();
        myDialog.show()
    }

    private fun startBootAnimation() {
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
