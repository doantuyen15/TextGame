package com.exercises.textgame


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.exercises.textgame.adapters.LobbyAdapter
import com.exercises.textgame.fragment.AlertDialogFragment
import com.exercises.textgame.models.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_lobby.*


class LobbyActivity : BaseActivity() {

    companion object{
        const val TAG = "LOBBY ACTIVITY"
    }
    private lateinit var adapter : LobbyAdapter
    val roomList: ArrayList<RoomInfo> = ArrayList()
    val roomKeyList = mutableListOf<String>()
    private var uid = ""
    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        setProgressBar(progressBar)
        setDialogAlert(dialogListener)
        checkNetworkConnectivity()
        getCurrentUser()
        listenForLobby()

        adapter = LobbyAdapter(this, roomList, listener)
        rvGameRoomList.adapter = adapter
        edtRoomTitle.hint = userName

        //create room on server ./gamerooms/$roomId
        btCreateGame.setOnClickListener {
            createNewRoom()
        }
    }


    private fun getCurrentUser(){
        val bundle = intent.extras
        //val currentUser = HashMap<String, Any>()
        bundle?.let {
            uid = it.getString(USER_UID_KEY).toString()
            userName = it.getString(USER_USERNAME_KEY).toString()
            //currentUser.put(userName, uid)
        }
        //return currentUser
    }

    private val lobbyEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
            //
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            //
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//            Log.d(LobbyActivity::class.java.simpleName, "Lobbychanged****************${p0}")
            val keyChange = p0.key.toString()
            roomList[roomKeyList.indexOf(keyChange)] = p0.getValue(RoomInfo::class.java)!!
            adapter.notifyItemChanged(roomKeyList.indexOf(keyChange))
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            if(p0.key != PREVENT_REMOVE_KEY) {
                addNewRoom(p0)
            }
//            Log.d(LobbyActivity::class.java.simpleName,"Lobbychanged****************${p1}")
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            val index = roomKeyList.indexOf(p0.key)
            roomKeyList.removeAt(index)
            roomList.removeAt(index)
            adapter.notifyItemRemoved(index)
        }

    }

    private fun listenForLobby(){
        roomRef.child(CHILD_LISTROOMS_KEY).addChildEventListener(lobbyEventListener)
    }

    private fun updateLobby(p0: DataSnapshot) {
        val roomInfo = p0.getValue(RoomInfo::class.java)
        val index = roomKeyList.indexOf(p0.key)
        if (roomInfo != null) {
            roomList[index] = roomInfo
            adapter.notifyItemChanged(index)
        }
    }

    private fun addNewRoom(p0: DataSnapshot) {
        val roomInfo = p0.getValue(RoomInfo::class.java)
        p0.key?.let { roomKeyList.add(it) }
        if (roomInfo != null) {
            roomList.add(roomInfo)
            adapter.notifyItemInserted(roomList.indexOf(roomInfo))
            rvGameRoomList.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun createNewRoom(){
        showProgressBar()
//        btCreateGame.isEnabled = false

//        val hostName = userName
        val roomTitle = if (edtRoomTitle.text.toString() == ""){
            "${userName}'s room"
        } else {
            edtRoomTitle.text.toString()
        }
        val roomKey = roomRef.push().key

        startGameActivity(roomKey, roomTitle, host = true)
        goneProgressBar()

    }
    private val listener = object : LobbyAdapter.OnClickListener {
        override fun onClick(index: Int) {
            if(roomList[index].roomStatus != "playing") {
                startGameActivity(roomKeyList[index])
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun startGameActivity(roomKey: String?, roomTitle: String?=null, host: Boolean = false){
        val intent = Intent(this, GameActivity::class.java)
        val dialog = AlertDialogFragment()
        dialog.show(supportFragmentManager, "loading")

        roomKey?.let { key ->
            intent.putExtra(ROOM_INFO_KEY, key)
            intent.putExtra(USER_UID_KEY, uid)
            intent.putExtra(USER_USERNAME_KEY, userName)
//            val defaultUserStatus = HashMap<String, Any?>()
//            defaultUserStatus[uid] = PlayerStatus(userName)
            val user = HashMap<String, String>()
            user[uid] = userName

            if (host) { //create new room
                val createRoomInfo =  RoomInfo(userName, roomTitle, QUIZ_GAME_KEY, user)
                roomRef.apply {
                    child(key)
                        .setValue(createRoomInfo)
                        .addOnSuccessListener {
                            userRef.child(uid).child(CHILD_CURRENTROOMID_KEY).setValue(key)
                            startActivity(intent)
                            finish()
                        }
                    child(CHILD_LISTROOMS_KEY)
                        .updateChildren(mapOf(key to createRoomInfo))
                }
            } else {
                dbGetRefRoom(key).child(CHILD_JOINEDUSER_KEY)
                    .child(uid)
                    .setValue(userName)
                    .addOnSuccessListener {
                        userRef.child(uid).child(CHILD_CURRENTROOMID_KEY).setValue(key)
                        startActivity(intent)
//                        dialog.setDismiss()
                        finish()
                    }
            }
        }
    }

    private fun removeFirebaseListener() {
        roomRef.removeEventListener(lobbyEventListener)
    }

    private val dialogListener = object: AlertDialogFragment.DetachDialogListener {
        override fun onDetachDialog() {
            finish()
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
            .setMessage("Go back to menu?")
            .setNegativeButton("Yes") { _, _ ->
                setResult(Activity.RESULT_OK)
                super.onBackPressed()
                finish()
            }
            .setPositiveButton("Dismiss") { dialog, _ -> dialog?.dismiss() }
        val dialog = builder.create();
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeNetworkListener()
        removeFirebaseListener()
    }
}

