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
    var currentUser : UserInfo? = UserInfo()
    var fireBaseAuthInstance = FirebaseAuth.getInstance()
    var fireBaseDataBaseInstance = FirebaseDatabase.getInstance()
    val userRef = fireBaseDataBaseInstance.getReference("/users")
    var valid : Boolean = true

    private var progressBar: ProgressBar? = null

    fun dbGetRefUser(ref : String): DatabaseReference {
        return userRef.child(ref)
    }

    //create room named by user's name
    fun dbGetRefRoom(ref: String? = null): DatabaseReference{
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
        currentUser?.uid = auth.uid
        fireBaseDataBaseInstance.getReference("/users")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach{
                        Log.d(BaseActivity::class.java.simpleName, "${it.getValue(UserInfo::class.java)}**********************")
                        if(it.value == currentUser?.uid){
                            currentUser?.username = it.key
                        }
                    }
//                    Log.d(BaseActivity::class.java.simpleName, "$p0********************** ${currentUser?.username}")
                }
            })
//        addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                // connect error, NEED function here
//            }
//            override fun onDataChange(p0: DataSnapshot) {
//                currentUser = p0.getValue(UserInfo::class.java)
//            }
//        })
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
class UserName(val username: String?, val isExist: Boolean? = true)
data class UserInfo(var username: String? =null, var uid : String? =null) // user holder
data class PlayerInfo(val playerName: String?="Player", val playerHp: Int?=100) // player holder
data class RoomInfo(val hostName: String?=null, val joinedUser: UserInfo?, val roomTitle: String?="${hostName}'s room", val gameType: String) {
    constructor() : this("",null,"","")
}

