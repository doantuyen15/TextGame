package com.exercises.textgame

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.exercises.textgame.fragment.AlertDialogFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import java.lang.Exception


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
    private var isReconnect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkListener = object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if(isReconnect) { // run below function when reconnecting
                //                reconnectToServer()
                fireBaseDataBaseInstance.goOnline()
                connectedRef.addValueEventListener(firebaseConnectivityListener)
            }
            isReconnect = true
        }
        override fun onLost(network: Network?) {
            showOrHideProgressDialog(false)
            fireBaseDataBaseInstance.goOffline()
            removeNetworkListener(true)
        }
    }

//    private fun reconnectToServer() {
//        //send command to reconnect
//        val command = HashMap<String, String>()
//        command[COMMAND_RECONNECTED_KEY] = "hihi"
//        var isDone = false
//        repeat(3) {
//            if (isDone) {
//                return
//            } else {
//                commandRef.setValue(command)
//                    .addOnCompleteListener {
//                        isDone = true
//                    }
//            }
//        }
//
//    }

    fun setDialogAlert(mListener: AlertDialogFragment.DetachDialogListener){
        dialog = AlertDialogFragment(mListener)
    }

    fun checkNetworkConnectivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(networkListener)
        }
    }

    fun removeNetworkListener(firebase: Boolean = false) {
        if(firebase){
            connectedRef.removeEventListener(firebaseConnectivityListener)
        } else {
            try{
                cm.unregisterNetworkCallback(networkListener)
            } catch (e: Exception) {}
        }
    }

    fun checkGooglePlayService(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            return false
        }
        return true
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

    private val firebaseConnectivityListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onDataChange(p0: DataSnapshot) {
            val connected = p0.getValue(Boolean::class.java) ?: false
            if (connected) {
                showOrHideProgressDialog(true)
                removeNetworkListener(true)
            }
        }

    }

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
const val USER_EMAIL_KEY = "USER_EMAIL_KEY"
const val USER_USERNAME_KEY = "USER_USERNAME_KEY"
const val ROOM_INFO_KEY = "ROOM_INFO_KEY"
const val QUIZ_GAME_KEY = "Quiz"
const val CHILD_USERNAME_KEY = "username"
const val LANGUAGE_EN_KEY = "en_US"
const val REQUEST_SPEECH_CODE = 3000
const val CHILD_CURRENTROOMID_KEY = "currentRoomId"
const val PREVENT_REMOVE_KEY = "preventRemoveKey"
const val CHILD_LISTROOMS_KEY = "listrooms"
const val FIRECLOUD_DATABASE_INFO = "databaseInfo"
const val COMMAND_ATTACK_KEY = "attack"
const val COMMAND_START_KEY = "start"
const val COMMAND_HELP_KEY = "help"
const val COMMAND_DISCONNECTED_KEY = "disconnected"
const val COMMAND_RECONNECTED_KEY = "reconnected"
const val COMMAND_QUIT_KEY = "quit"
const val COMMAND_STAY_KEY = "open"
const val COMMAND_KICK_KEY = "kick"
const val AppConfig = "config"
const val CURRENT_DATABASE_VERSION = "CURRENT_DATABASE_VERSION"