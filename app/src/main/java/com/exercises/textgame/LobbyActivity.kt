package com.exercises.textgame


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_lobby.*
import kotlinx.android.synthetic.main.room_item.view.*



class LobbyActivity : BaseActivity() {

    companion object{
        val TAG = "LOBBY ACTIVITY"
    }
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        setProgressBar(progressBar)


        listenForLobby()
        rvGameRoomList.adapter = adapter
/*get current room list on server
        fireBaseDataBaseInstance.getReference("gamerooms")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    //
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val roomInfo = it.getValue(RoomInfo::class.java)
                        if (roomInfo != null) {
                            validateLayoutAdapter(roomInfo)
                        }
                    }
                }
            })

*/
        //create room on server ./gamerooms/$username
        btCreateGame.setOnClickListener {
            showProgressBar()
            createNewRoom()
        }
    }
    private fun listenForLobby(){
        val refLobby = fireBaseDataBaseInstance.getReference("gamerooms")
        refLobby.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "loadPost:onCancelled", p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val roomInfo = p0.getValue(RoomInfo::class.java)
                if (roomInfo != null) {
                    Log.d(TAG,"************************${roomInfo.roomTitle}")
                    adapter.add(LobbyHolder(roomInfo))
                }
            }
        })
/*        refLobby.addChildEventListener(object: ChildEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//                //
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                //
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                //val roomInfo = p0.getValue(RoomInfo::class.java)
//            }
//
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                val roomInfo = p0.getValue(RoomInfo::class.java)
//                if (roomInfo != null) {
//                    Log.d(TAG,"************************${roomInfo.roomTitle}")
//                    adapter.add(LobbyHolder(roomInfo))
//                }
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//                //
//            }
//
       })*/
    }
    //private fun displayLobby
    private fun startProfileActivity(movie: RoomInfo){
        //val intent = Intent(this, ProfileActivity::class.java)
        //intent.putExtra(MOVIE_TITLE_KEY, movie.title)

        startActivity(intent)
    }

    private fun createNewRoom(){
        btCreateGame.isEnabled = false
        edtRoomTitle.isEnabled = false
        val roomTitle: String? = edtRoomTitle.text.toString()
        val roomInfo = RoomInfo(currentUser?.username, UserInfo(), roomTitle, "quiz")
        //Log.d(GameActivity::class.java.simpleName,"************************$roomInfo")
        dbGetRefRoom(currentUser?.username)
            .setValue(roomInfo)
            .addOnCompleteListener {
                btCreateGame.isEnabled = true
                edtRoomTitle.isEnabled = true
                Toast.makeText(this,"Room created!", Toast.LENGTH_LONG).show()
                goneProgressBar()
            }
    }

    class LobbyHolder(private val roomInfo: RoomInfo): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.room_item
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.tvRoomTitle.text = roomInfo.roomTitle
            viewHolder.itemView.tvRoomType.text = roomInfo.gameType
        }

    }
}
