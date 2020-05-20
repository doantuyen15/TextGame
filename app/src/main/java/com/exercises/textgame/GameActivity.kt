package com.exercises.textgame

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.*
import com.exercises.textgame.adapters.ChatLogAdapter
import com.exercises.textgame.adapters.GameAdapter
import com.exercises.textgame.models.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_lobby.*
import java.lang.Exception


class GameActivity : BaseActivity() {

    private lateinit var adapter : GameAdapter
    private lateinit var adapterChatLog : ChatLogAdapter
    private val playerListStatus = ArrayList<PlayerStatus?>()
    private var playerIndex = ArrayList<String?>()
//    private var playerNameList = ArrayList<String?>()
    private var playerList = HashMap<String,String>()
    private val chatLog = LinkedHashMap<String, Message?>()
    private val keyLogs = ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val data = intent.extras
        val joinedRoomKey = data?.getString(ROOM_INFO_KEY)
        val uid = data?.getString(USER_UID_KEY)
        fetchCurrentRoomInfo(joinedRoomKey)
//        addDummyPlayer()

        adapter = GameAdapter(this, playerListStatus)
        adapterChatLog = ChatLogAdapter(this, chatLog)
        rvPlayerList.adapter = adapter
        rvChatLog.adapter = adapterChatLog

        // open/close slider bar Player list status
        sliderButton.setOnClickListener{
            sliderButton.isEnabled = false
            getSliderBar()
        }
        //Send message
        validateMessage()
        btnSendMessage.setOnClickListener {
            if (uid != null) {
                sendMessage(uid, joinedRoomKey, edtMessage.text.toString())
            }
        }
    }

    private fun validateMessage() {
        edtMessage.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnSendMessage.isEnabled = false
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnSendMessage.isEnabled = s.toString().trim().isNotBlank()
            }
        })
    }

    private fun sendMessage(uid: String, roomKey: String?, message: String) {
//        val mapMessage = HashMap<String,String>()
        var keyLog = ""
        if (roomKey != null) {
            try{
                keyLog = dbGetRefRoom(roomKey).push().key!!
                keyLogs.add(keyLog)
            } catch (e : Exception){
                Log.e(GameActivity::class.java.simpleName, "Changed*****************************${e.message}")
            }
//            mapMessage[playerList[uid].toString()] = message
            chatLog[keyLog] = Message(playerList[uid].toString(), message)
            //chatLog.add(mMap)
            Log.d(GameActivity::class.java.simpleName, "Changed*****************************${chatLog.size}")
            adapterChatLog.notifyItemInserted(adapterChatLog.itemCount)
            rvChatLog.scrollToPosition(adapterChatLog.itemCount - 1)
            edtMessage.text.clear()
//            val keyLog = dbGetRefRoom(roomKey).push().key
            dbGetRefRoom(roomKey)
                .child(CHILD_MESSAGE_KEY)
                .setValue(chatLog)
        }
    }

    private fun fetchCurrentRoomInfo(roomKey: String?){
        if (roomKey != null) {
            dbGetRefRoom(roomKey).addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
//                    TODO("Not yet implemented")
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                    TODO("Not yet implemented")
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    if(p0.key == CHILD_ATTACKER_KEY){
//                        updateUserStatus(p0.child(CHILD_JOINEDUSER_KEY))
                    }
                    if(p0.key == CHILD_JOINEDUSER_KEY){
//                        updateUserStatus(p0.child(CHILD_JOINEDUSER_KEY))
                    }
                    if(p0.key == CHILD_USERSTATUS_KEY){
                        updateUserStatus(roomKey)
                    }
                    if(p0.key == CHILD_MESSAGE_KEY){
                        updateLastMessage(p0.children)
                    }
//                    Log.d(GameActivity::class.java.simpleName, "Changed*****************************${p0.key}")
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    if (p0.key == CHILD_USERSTATUS_KEY){
                        refreshAdapterStatus(p0.children)
//                        Log.d(GameActivity::class.java.simpleName, "Added*****************************${p0.key}")
                    }
                    if (p0.key == CHILD_JOINEDUSER_KEY) {
                        fetchUser(p0.children)
                    }
//                    Log.d(GameActivity::class.java.simpleName, "Added*****************************${p0.key}")
                }

                override fun onChildRemoved(p0: DataSnapshot) {
//                    TODO("Not yet implemented")
                }

            })
        }
//        adapter.add(RoomHolder(playerList))
    }

    private fun updateLastMessage(newMessage: MutableIterable<DataSnapshot>) {
        newMessage.forEach{
            val newKey = it.key
            if(!keyLogs.contains(newKey)){
                keyLogs.add(newKey)
                chatLog[newKey.toString()] = it.getValue(Message::class.java)
                adapterChatLog.notifyItemInserted(keyLogs.indexOf(newKey))
                rvChatLog.scrollToPosition(adapterChatLog.itemCount - 1)
            }
        }
    }

    private fun refreshAdapterStatus(playerStatus: MutableIterable<DataSnapshot>, isUpdate: Boolean=false){
        //key=uid
        if(isUpdate){
            playerListStatus.clear()
            playerIndex.clear()
        }
        playerStatus.forEach {
            if(isUpdate){
                playerIndex.add(it.key)
//                Log.d(GameActivity::class.java.simpleName, "Changed*****************************$playerIndex")
            }
            val index = playerIndex.indexOf(it.key)
            playerListStatus.add(it.getValue(PlayerStatus::class.java))
        }
        getSliderBar(true)
        adapter.notifyDataSetChanged()
    }

    /*private fun updateUserStatus(newPlayerStatus: MutableIterable<DataSnapshot>) {
        newPlayerStatus.forEach {
            val index = playerIndex.indexOf(it.key)
            Log.d(
                GameActivity::class.java.simpleName,
                "update*****************************${p0.value}"
            )
            try {
                playerList[index] = it.getValue(PlayerStatus::class.java)
                adapter.notifyItemChanged(index)
            } catch (e: Exception) {
                Log.e(
                    GameActivity::class.java.simpleName,
                    "update*****************************${e}"
                )
            }
        }
    }*/
    private fun updateUserStatus(roomKey: String?) {
        if (roomKey != null) {
            dbGetRefRoom(roomKey)
                .child(CHILD_USERSTATUS_KEY)
                .orderByChild(CHILD_PLAYERHP_KEY)
                .limitToLast(101)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
//
                    }
                    override fun onDataChange(p0: DataSnapshot) {
//                        fetchUser(p0.children, true)
                        refreshAdapterStatus(p0.children, true)
                    }
                })
        }
    }

    private fun addDummyPlayer(){
//        adapter.add(RoomHolder(this, PlayerInfo()))
    }

    private fun fetchUser(newJoinedPlayer: MutableIterable<DataSnapshot>, isSorted: Boolean=false) {
//        playerListStatus.clear()
//        playerIndex.clear()
//        playerNameList.clear()
//        playerIndex.add(it.key)
//        if(isSorted) {
//            playerIndex.clear()
//            playerList.clear()
//        }
        // uid = player name
        newJoinedPlayer.forEach {
            val newKey = it.key.toString()
            if (!playerList.containsKey(newKey)) {
                playerList[newKey] = it.value as String
                playerIndex.add(newKey)
            }
        }
    }

    private fun getSliderBar(isRefresh: Boolean = false){
        val animSwipeLeft = AnimationUtils.loadAnimation(this, R.anim.left_fade_out)
        val animSwipeRight = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_fade_in)
        if (rvPlayerList.visibility == View.VISIBLE && !isRefresh){
            //close slide bar
            rvPlayerList.startAnimation(animSwipeLeft)
            animSwipeLeft.setAnimationListener(object: Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {
                    //
                }

                override fun onAnimationEnd(animation: Animation?) {
                    rvPlayerList.visibility = View.GONE
                    sliderButton.isEnabled = true
                }

                override fun onAnimationStart(animation: Animation?) {
                    sliderButton.animate().rotation(0F).interpolator = DecelerateInterpolator()
                }
            })
        } else {
            rvPlayerList.visibility = View.VISIBLE
            rvPlayerList.layoutAnimation = animSwipeRight
            rvPlayerList.layoutAnimationListener = object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {
                    //
                }

                override fun onAnimationEnd(animation: Animation?) {
                    sliderButton.isEnabled = true
                }

                override fun onAnimationStart(animation: Animation?) {
                    sliderButton.animate().rotation(180F).interpolator = LinearInterpolator()
                }
            }
        }
    }

//    class RoomHolder(private val playerInfo: ArrayList<PlayerInfo?>): Item<ViewHolder>(){
//
//        override fun getLayout(): Int {
//            return R.layout.player_item
//        }
//
//        override fun bind(viewHolder: ViewHolder, position: Int) {
//            val data = playerInfo[position]
//            if (data != null) {
//                viewHolder.itemView.tvPlayerName.text = data.playerName
//                viewHolder.itemView.progressBarPlayer.progress = data.playerHp ?: 0
//            }
//        }
//
//    }
}
