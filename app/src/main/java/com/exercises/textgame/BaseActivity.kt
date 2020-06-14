package com.exercises.textgame

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.exercises.textgame.fragment.AlertDialogFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(){
    private var auth = Firebase.auth
    private var currentUser = auth.currentUser
    var fireBaseAuthInstance = FirebaseAuth.getInstance()
    var fireBaseDataBaseInstance = FirebaseDatabase.getInstance()
    val userRef = fireBaseDataBaseInstance.getReference("/users")
    val roomRef = fireBaseDataBaseInstance.getReference("/gamerooms")
    val commandRef = fireBaseDataBaseInstance.getReference("/command")
    val connectedRef = fireBaseDataBaseInstance.getReference(".info/connected")
    val dbQuiz = FirebaseFirestore.getInstance()
    var valid : Boolean = true
    private var progressBar: ProgressBar? = null
//    private val dialog = AlertDialogFragment()
    private lateinit var dialog: AlertDialogFragment
    private lateinit var cm : ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkListener = object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            showOrHideProgressDialog(true)
            fireBaseDataBaseInstance.goOnline()
        }
        override fun onLost(network: Network?) {
            showOrHideProgressDialog(false)
            fireBaseDataBaseInstance.goOffline()
        }
    }

    fun setDialogAlert(mListener: AlertDialogFragment.DetachDialogListener){
        dialog = AlertDialogFragment(mListener)
    }

    fun checkNetworkConnectivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(networkListener)
        }
    }

    fun removeNetworkListener() {
        cm.unregisterNetworkCallback(networkListener)
    }

    fun checkGooglePlayService(): Boolean {
        val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
        return status == ConnectionResult.SUCCESS
    }

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

    private fun showOrHideProgressDialog(isConnected: Boolean) {
        runOnUiThread {
            if (!isConnected) {
                if (dialog.isAdded) {
                    dialog.setState(isConnected)
                } else {
                    dialog.show(supportFragmentManager, "TAG")
                }
//            Log.d("***********BaseActivity", "disconnected, dialog show = ${dialog.isAdded}")
            } else {
                dialog.setState(isConnected)
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