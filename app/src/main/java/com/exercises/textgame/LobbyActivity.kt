package com.exercises.textgame


import android.content.Intent
import android.os.Bundle
import com.exercises.textgame.adapters.LobbyAdapter
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

        getCurrentUser()
        listenForLobby()

        adapter = LobbyAdapter(
            this,
            roomList,
            listener
        )

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

    private fun listenForLobby(){
        roomRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                //
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                if(p0.key == CHILD_HOSTNAME_KEY || p0.key == CHILD_TITLE_KEY){
                    updateLobby(p0)
                }
//                Log.d(LobbyActivity::class.java.simpleName,"Lobbychanged****************${p0.key}")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                addNewRoom(p0)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val index = roomKeyList.indexOf(p0.key)
                roomKeyList.removeAt(index)
                roomList.removeAt(index)
                adapter.notifyItemRemoved(index)
            }

       })
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

        val hostName = userName
        val roomTitle = if (edtRoomTitle.text.toString() == ""){
            "${userName}'s room"
        } else {
            edtRoomTitle.text.toString()
        }
        val roomKey = roomRef.push().key

        startGameActivity(roomKey, hostName, roomTitle)
        goneProgressBar()

    }
    private val listener = object : LobbyAdapter.OnClickListener {
        override fun onClick(index: Int) {
            if(roomList[index].roomStatus != "playing") {
                startGameActivity(roomKeyList[index])
            }
        }
    }

    private fun startGameActivity(roomKey: String?, hostName: String?=null, roomTitle: String?=null){
        val intent = Intent(this, GameActivity::class.java)
        roomKey?.let {
            intent.putExtra(ROOM_INFO_KEY, it)
            intent.putExtra(USER_UID_KEY, uid)
            intent.putExtra(CHILD_HOSTNAME_KEY, hostName)
            val defaultUserStatus = HashMap<String, Any?>()
            defaultUserStatus[uid] = PlayerStatus(userName)
            val user = HashMap<String, String>()
            user[uid] = userName

            if (hostName != null) { //create new room
                val createRoomInfo =  RoomInfo(hostName, roomTitle, QUIZ_GAME_KEY, user, defaultUserStatus)
                dbGetRefRoom(it)
                    .setValue(createRoomInfo)
                    .addOnSuccessListener {
                        startActivity(intent)
                        finish()

                    }
            } else {
                val userJoinToRoom = HashMap<String, Any?>()
                userJoinToRoom[CHILD_JOINEDUSER_KEY] = user
                dbGetRefRoom(it)
                    .child(CHILD_JOINEDUSER_KEY)
                    .updateChildren(user as Map<String, Any>)
                dbGetRefRoom(it)
                    .child(CHILD_USERSTATUS_KEY)
                    .updateChildren(defaultUserStatus)
                    .addOnSuccessListener {
                        startActivity(intent)
                        finish()
                    }
            }
        }
    }
}

