package com.exercises.textgame


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        setProgressBar(progressBar)

        listenForLobby()
        adapter = LobbyAdapter(this, roomList, listener)
        rvGameRoomList.adapter = adapter

        //create room on server ./gamerooms/$roomId
        btCreateGame.setOnClickListener {
            createNewRoom()
        }
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
                val changedData = p0.getValue(RoomInfo::class.java)
                val index = roomList.indexOf(changedData)
                roomList[index] = changedData!!
                adapter.notifyItemChanged(index)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val roomInfo = p0.getValue(RoomInfo::class.java)
                if (roomInfo != null) {
                    roomList.add(roomInfo)
                    adapter.notifyItemInserted(roomList.indexOf(roomInfo))
                    rvGameRoomList.scrollToPosition(adapter.itemCount - 1)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val index = roomList.indexOf(p0.getValue(RoomInfo::class.java))
                //adapter.removeGroup(index)
                adapter.notifyItemRemoved(index)
            }

       })
    }

    private fun createNewRoom(){
        showProgressBar()
        val data = intent.extras
        val userName = data?.getString("USER_USERNAME_KEY")
        btCreateGame.isEnabled = false
        val roomTitle = if (edtRoomTitle.text.toString() == ""){
            "$userName's room"
        } else {
            edtRoomTitle.text.toString()
        }
        Log.d(LobbyActivity::class.java.simpleName,"************************$roomTitle")
        val roomInfo = RoomInfo(userName, roomTitle, QUIZ_GAME_KEY)
        //        Log.d(LobbyActivity::class.java.simpleName,"************************$userName")
        roomRef.push()
            .setValue(roomInfo)
            .addOnCompleteListener {
                btCreateGame.isEnabled = true
                edtRoomTitle.text.clear()
                startGameActivity(roomInfo)
                goneProgressBar()
            }
    }
    private val listener = object : LobbyAdapter.OnClickListener {
        override fun onClick(room: RoomInfo) {
//            Toast.makeText(this@LobbyActivity,"go go go gogoooooo",Toast.LENGTH_LONG).show()
            startGameActivity(room)
        }
    }

    private fun startGameActivity(room: RoomInfo){
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(ROOM_INFO_KEY, room.roomTitle)
        startActivity(intent)
    }

}

