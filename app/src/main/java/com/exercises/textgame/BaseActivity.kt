package com.exercises.textgame

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

open class BaseActivity : AppCompatActivity() {

    var auth: FirebaseAuth = Firebase.auth
    var fireBaseAuthInstance = FirebaseAuth.getInstance()
    var fireBaseDataBaseInstance = FirebaseDatabase.getInstance()
    var rootRef = FirebaseDatabase.getInstance().getReference().child("users")
    var valid : Boolean = true

    private var progressBar: ProgressBar? = null

    fun setProgressBar(bar: ProgressBar) {
        progressBar = bar
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.INVISIBLE
    }

    fun checkUserNameAlreadyExist(fullName: String): Boolean {
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                this@BaseActivity.valid = !p0.hasChild("$fullName")
            }

        })
        return valid
    }
}
class UserInfo(val uid : String, val username : String)