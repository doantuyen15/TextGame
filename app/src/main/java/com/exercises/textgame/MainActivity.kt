package com.exercises.textgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)

    }
}
