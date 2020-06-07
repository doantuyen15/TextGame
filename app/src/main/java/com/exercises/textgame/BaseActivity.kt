package com.exercises.textgame

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

//    init {
//        FirebaseApp.initializeApp(this)
//    }

    private var auth = Firebase.auth
    private var currentUser = auth.currentUser
    var fireBaseAuthInstance = FirebaseAuth.getInstance()
    private var fireBaseDataBaseInstance = FirebaseDatabase.getInstance()
    val userRef = fireBaseDataBaseInstance.getReference("/users")
    val roomRef = fireBaseDataBaseInstance.getReference("/gamerooms")
    val commandRef = fireBaseDataBaseInstance.getReference("/command")
    val connectedRef = fireBaseDataBaseInstance.getReference(".info/connected")
    val dbQuiz = FirebaseFirestore.getInstance()
    var valid : Boolean = true

    private var progressBar: ProgressBar? = null

    fun dbGetRefUser(ref : String): DatabaseReference {
        return userRef.child(ref)
    }

    fun dbGetRefRoom(ref: String): DatabaseReference{
        return roomRef.child(ref)
    }

    fun getDbQuiz(ref: String): CollectionReference {
        return dbQuiz.collection(ref)
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
//        if(currentUser?.uid == null) return
//        else {
//            currentUser?.uid = auth?.uid
//            dbGetRefUser(auth?.uid!!)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onCancelled(p0: DatabaseError) {
//                        //
//                    }
//
//                    override fun onDataChange(p0: DataSnapshot) {
//                        currentUser?.username = p0.child("username").value as String?
////                        Log.d(BaseActivity::class.java.simpleName,"*******************$currentUser")
//                    }
//
//                })
            currentUser?.let {
                for (profile in it.providerData) {
                    // Id of the provider (ex: google.com)
                    val providerId = profile.providerId
                    // UID specific to the provider
                    val uid = profile.uid
                    // Name, email address, and profile photo Url
                    val name = profile.displayName
                    val email = profile.email
                    val photoUrl = profile.photoUrl
                }
            }
        }
//        addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                // connect error, NEED function here
//            }
//            override fun onDataChange(p0: DataSnapshot) {
//                currentUser = p0.getValue(UserInfo::class.java)
//            }
//        })

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
//class UserName(val username: String?, val isExist: Boolean? = true)
data class UserInfo(var username: String? =null, var uid : String? =null) // user holder
//data class RoomInfo(val hostName: String?=null, val roomTitle: String?=null, val gameType: String, val joinedUser: Any?=null) {
//    constructor() : this("","","",null)
//}
const val USER_UID_KEY = "USER_UID_KEY"
const val USER_USERNAME_KEY = "USER_USERNAME_KEY"
const val ROOM_INFO_KEY = "ROOM_INFO_KEY"
const val QUIZ_GAME_KEY = "Quiz"
const val CHILD_USERNAME_KEY = "username"
const val LANGUAGE_EN_KEY = "en_US"
const val REQUEST_SPEECH_CODE = 3000