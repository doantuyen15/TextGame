package com.exercises.textgame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.exercises.textgame.fragment.AlertDialogFragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    //fetch current user
    private val currentUser = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setDialogAlert(detachListener)
//        checkNetworkConnectivity()

        btnPlay.setOnClickListener {
            FirebaseDatabase.getInstance().goOffline()
            FirebaseDatabase.getInstance().goOnline()
            loadingFrame.visibility = View.VISIBLE
            btnPlay.isEnabled = false
            startGame()
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        Sign_out.setOnClickListener {
            signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_CANCELED) {//game activity
            clearUserCurrentRoom()
        }
        if (requestCode == 456 && resultCode == Activity.RESULT_CANCELED) {
            clearUserCurrentRoom()
            Log.d("main activity", "return from Lobby")
        } else {
            btnPlay.isEnabled = true
        }
    }

    private fun clearUserCurrentRoom() {
        Log.d("main activity", "return from Game")
        userRef.child(currentUser!!.uid).setValue(null)
            .addOnCompleteListener {
                Log.d("main activity", "remove current id")
                btnPlay.isEnabled = true
            }
            .addOnFailureListener {
                Log.d("main activity", "failed with ${it.message}")
            }
    }

    private fun signOut() {
        showProgressBar()
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                hideProgressBar()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }

    private fun startGame() {
        val userId = currentUser?.uid
        val displayName = currentUser?.displayName
        val lobby = Intent(this, LobbyActivity::class.java)
        lobby.putExtra(USER_UID_KEY, userId)
        lobby.putExtra(USER_USERNAME_KEY, displayName)
        val gameActivity = Intent(this, GameActivity::class.java)
        gameActivity.putExtra(USER_USERNAME_KEY, displayName)
        gameActivity.putExtra(USER_UID_KEY, userId)
        var roomKey = ""
        if (userId != null) {
            val ref = userRef
//            ref.keepSynced(true)
            ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("MainActivity Start Game", "error at $p0")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.d("MainActivity Start Game", "got user last room key")
                    if (p0.hasChild(CHILD_CURRENTROOMID_KEY)) {
                        roomKey = p0.child(CHILD_CURRENTROOMID_KEY).value.toString()
                        gameActivity.putExtra(ROOM_INFO_KEY, roomKey)
//                        gameActivity.putExtra(USER_USERNAME_KEY, displayName)
                        checkExistRoom(roomKey, gameActivity, lobby)
                    } else {
                        startActivityForResult(lobby, 456)
                        loadingFrame.visibility = View.INVISIBLE
                    }
                }
            })
        }
    }

    private fun checkExistRoom(
        roomKey: String,
        gameActivity: Intent,
        lobby: Intent
    ) {
        roomRef.child(CHILD_LISTROOMS_KEY).orderByKey().equalTo(roomKey).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("MainActivity Start Game", "error at $p0")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("MainActivity Start Game", "key available")
                if(p0.hasChild(roomKey)){
                    startActivityForResult(gameActivity, 123)
                } else {
                    userRef.child(currentUser!!.uid).setValue(null)
                    startActivityForResult(lobby, 456)
                }
                loadingFrame.visibility = View.INVISIBLE
            }

        })
    }

    private val detachListener = object: AlertDialogFragment.DetachDialogListener {
        override fun onDetachDialog() {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "activity pause")
    }

//    override fun onDetachDialog() {
//        finish()
//    }
}
