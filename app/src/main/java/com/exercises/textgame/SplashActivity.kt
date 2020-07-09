package com.exercises.textgame

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {
//    private val fireCloud = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkDatabaseVersion()

        val bootCheck = checkGooglePlayService()
        if (!bootCheck) {
            showBootCheckDialog()
        } else {
            Log.d("checkDatabase*********", "checked")
            startBootAnimation()
        }
    }

    private fun checkDatabaseVersion() {
        Log.d("checkDatabase*********", "checking")
        val savedCurrentVersion = getSharedPreferences(AppConfig, Context.MODE_PRIVATE)
        val currentVersion = savedCurrentVersion.getString(CURRENT_DATABASE_VERSION, "firstRun")
        if (currentVersion == "firstRun") {
            Log.d("checkDatabase*********", "current version: ${currentVersion.toString()}")
            Firebase.firestore.firestoreSettings =
                firestoreSettings {
                    isPersistenceEnabled = true
                    setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
                }
        }
        dbQuiz.collection(FIRECLOUD_DATABASE_INFO).document("version")
            .get(Source.SERVER)
            .addOnSuccessListener { result ->
                val version = result.data!!.keys.first().toString()
                if(currentVersion != version){
                    updateDatabase(version)
                    Log.d("checkDatabase*********", "UPDATING DB")
                } else{
                    Log.d("checkDatabase*********", "DB is up to date")
                }
            }
    }

    private fun updateDatabase(version: String) {
        val savedCurrentVersion = getSharedPreferences(AppConfig, Context.MODE_PRIVATE)
        getDbQuiz("quiz")
            .get(Source.SERVER)
            .addOnSuccessListener {
                Log.d("checkDatabase*********", it.toString())
                savedCurrentVersion.edit()
                    .putString(CURRENT_DATABASE_VERSION, version)
                    .apply()
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
