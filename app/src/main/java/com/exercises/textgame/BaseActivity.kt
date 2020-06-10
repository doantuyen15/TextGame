package com.exercises.textgame

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
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
    private var mContext: Context? = null
    private lateinit var dialog: AlertDialog
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

//    fun getConnectionState() {
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val connected = snapshot.getValue(Boolean::class.java) ?: false
//                if (!connected) {
//                    showProgressDialog(true)
//                    Log.d("getConnectionState", "disconnected to server")
//                } else{
//                    showProgressDialog(false)
//                    Log.d("getConnectionState", "connected to server")
////                    if(removeListenerAfter) connectedRef.removeEventListener(listener)
////                    if(dialog != null && dialog.isShowing){
////                        dialog.dismiss()
////                    }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("getConnectionState", "Listener was cancelled with error: $error")
//            }
//        }
//        connectedRef.addValueEventListener(listener)
//    }

    fun fetchUsers(){
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

    fun setContext(ctx: Context){
        mContext = ctx
        dialog = AlertDialog.Builder(mContext!!)
            .setCancelable(false)
            .setView(R.layout.layout_loading_dialog)
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .create()
        Log.d("setContext", "${mContext}")
    }

    fun showProgressDialog(show: Boolean) {
//        Log.d("showDialog", mContext.packageName)
        Log.d("showDialog", "${mContext}")
        if(mContext != null){
            if(show){
                dialog.show()
            } else {
                if(dialog.isShowing) dialog.dismiss()
            }
        }
    }

}

const val USER_UID_KEY = "USER_UID_KEY"
const val USER_USERNAME_KEY = "USER_USERNAME_KEY"
const val ROOM_INFO_KEY = "ROOM_INFO_KEY"
const val QUIZ_GAME_KEY = "Quiz"
const val CHILD_USERNAME_KEY = "username"
const val LANGUAGE_EN_KEY = "en_US"
const val REQUEST_SPEECH_CODE = 3000
const val CHILD_CURRENTROOMID_KEY = "currentRoomId"
const val PREVENT_REMOVE_KEY = "preventRemoveKey"

const val COMMAND_ATTACK_KEY = "attack"
const val COMMAND_START_KEY = "start"
const val COMMAND_DISCONNECTED_KEY = "disconnected"