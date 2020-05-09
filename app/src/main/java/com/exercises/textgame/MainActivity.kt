package com.exercises.textgame

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logo.setOnClickListener{

            startActivity(Intent(this,GameActivity::class.java))
        }

        Sign_out.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, SignupActivity::class.java))
        }


    }
}
