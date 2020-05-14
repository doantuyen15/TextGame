package com.exercises.textgame

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {

    var auth: FirebaseAuth = Firebase.auth
    var currentUser : UserInfo? = null
    var fireBaseAuthInstance = FirebaseAuth.getInstance()
    var fireBaseDataBaseInstance = FirebaseDatabase.getInstance()
    val rootRef = fireBaseDataBaseInstance.getReference("/users")
    var valid : Boolean = true

    private var progressBar: ProgressBar? = null

    fun dbgetRefUser(ref : String): DatabaseReference {
        return fireBaseDataBaseInstance.getReference("/users/$ref")
    }

    //create room named by user's name
    fun dbgetRefRoom(ref: String? = null): DatabaseReference{
        return fireBaseDataBaseInstance.getReference("/gamerooms/$ref")
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

    fun goneProgressBar() {
        progressBar?.visibility = View.GONE
    }

    fun fetchUsers(){
        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // connect error, NEED function here
            }
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(UserInfo::class.java)
            }
        })
    }

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
class UserInfo(val uid : String?=null, val username: String?=null) // user holder
data class RoomInfo(val hostName: String?=null, val joinedUserId: String?=null, val roomTitle: String?="${hostName}'s room", val gameType: String) {
    constructor() : this("","","","")
}

