package com.exercises.textgame

import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private val currentUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logo.setOnClickListener{
            startGame()
        }

        Sign_out.setOnClickListener{
            signOut()
        }



    }

    private fun signOut(){
        showProgressBar()
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                hideProgressBar()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }

    private fun startGame(){
        val intent = Intent(this,LobbyActivity::class.java)
        intent.putExtra(USER_UID_KEY, currentUser?.uid)
        intent.putExtra(USER_USERNAME_KEY, currentUser?.displayName)
        startActivity(intent)
    }
}
