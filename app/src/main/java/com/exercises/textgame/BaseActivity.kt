package com.exercises.textgame

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {

    var auth: FirebaseAuth = Firebase.auth
    var signedUser : FirebaseUser? = auth.currentUser
    var fireBaseAuthInstance = FirebaseAuth.getInstance()
    var fireBaseDataBaseInstance = FirebaseDatabase.getInstance()
    var valid : Boolean = true

    private var progressBar: ProgressBar? = null

    fun getRef(ref : String): DatabaseReference {
        return fireBaseDataBaseInstance.getReference("/users/$ref")
    }
    fun setProgressBar(bar: ProgressBar) {
        progressBar = bar
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.INVISIBLE
    }

//    fun fetchUsers(){
//        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                val user = p0.getValue(UserInfo::class.java)
//                Log.d(TAG,"return valid = ${user?.username}")
//            }
//
//        })
//    }

//    fun checkUserNameAlreadyExist(fullName: String): Boolean {
//        rootRef.ref.setValue(user)
//            .addOnSuccessListener {
//                hideProgressBar()
//                valid
//            }
//            .addOnFailureListener {
//            }
//    }
    companion object {
        private const val TAG = "BASE"
    }
}
class UserInfo(val uid : String?=null)
class RoomInfo(val host: String?=null, val joinedUserId: String?=null)

