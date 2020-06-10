package com.exercises.textgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.exercises.textgame.fragment.AlertDialogFragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(){

    //fetch current user
    private val currentUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        getConnectionState()
        val dialog = AlertDialogFragment()
//        dialog.show(supportFragmentManager,"aa")
        btnPlay.setOnClickListener {
            progressBarMain.visibility = View.VISIBLE
            tvDialogConnecting.visibility = View.VISIBLE
            progressBarMain.playAnimation()
            btnPlay.isEnabled = false
            startGame()
        }

        Sign_out.setOnClickListener {
            signOut()
        }

        btnTest.setOnClickListener {
            dialog.show(supportFragmentManager,"aa")
        }

//        var fbquery = commandRef.child(COMMAND_DISCONNECTED_KEY).setValue("online")
//        commandRef.child(COMMAND_DISCONNECTED_KEY)
//            .onDisconnect()     // Set up the disconnect hook
//            .setValue("offline");

    }

    override fun onPause() {
        super.onPause()
        progressBarMain.visibility = View.INVISIBLE
        tvDialogConnecting.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        btnPlay.isEnabled = true
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
        var roomKey: String = ""
        if (userId != null) {
            userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("MainActivity Start Game", "error at $p0")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChild(CHILD_CURRENTROOMID_KEY)) {
                        roomKey = p0.child(CHILD_CURRENTROOMID_KEY).value.toString()
                        gameActivity.putExtra(ROOM_INFO_KEY, roomKey)
                        gameActivity.putExtra(USER_UID_KEY, userId)
//                        gameActivity.putExtra(USER_USERNAME_KEY, displayName)
                        checkExistRoom(roomKey, gameActivity, lobby)
                    } else {
                        startActivity(lobby)
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
        roomRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
//                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild(roomKey)){
                    startActivity(gameActivity)
                } else{
                    userRef.child(currentUser!!.uid).updateChildren(mapOf(CHILD_CURRENTROOMID_KEY to null))
                    startActivity(lobby)
                }
            }

        })
    }

//    private val dialog = AlertDialog.Builder(this)
//        .setCancelable(false)
//        .setView(R.layout.layout_loading_dialog)
//        .setNegativeButton("Cancel") { _, _ ->
//            finish()
//        }
//        .create()


}
