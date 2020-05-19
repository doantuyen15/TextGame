package com.exercises.textgame

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.*
import com.exercises.textgame.adapters.GameAdapter
import com.exercises.textgame.models.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : BaseActivity() {

    private lateinit var adapter : GameAdapter
    private val playerList = ArrayList<PlayerStatus?>()
    private var playerIndex = ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val data = intent.extras
        val roomKey = data?.getString(ROOM_INFO_KEY)
        fetchCurrentRoomInfo(roomKey)
//        addDummyPlayer()

        adapter = GameAdapter(this, playerList)
        rvPlayerList.adapter = adapter


        sliderButton.setOnClickListener{
            sliderButton.isEnabled = false
            getSliderBar()
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
                    if(p0.key == CHILD_USERSTATUS_KEY)
                        updateUserStatus(p0.children)
                    Log.d(GameActivity::class.java.simpleName, "Changed*****************************${p0.key}")
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    if (p0.key == CHILD_USERSTATUS_KEY) {
                        addNewUser(p0.children)
                    }
//                    Log.d(GameActivity::class.java.simpleName, "Added*****************************${p0}")
                }

                override fun onChildRemoved(p0: DataSnapshot) {
//                    TODO("Not yet implemented")
                }

            })
        }
//        adapter.add(RoomHolder(playerList))
    }

    private fun updateUserStatus(playerStatus: MutableIterable<DataSnapshot>) {
        playerStatus.forEach{
            val index = playerIndex.indexOf(it.key)
            val newPlayerStatus = it.getValue(PlayerStatus::class.java)
            playerList[index] = newPlayerStatus
            adapter.notifyItemChanged(index)
        }
    }

    private fun addDummyPlayer(){
//        adapter.add(RoomHolder(this, PlayerInfo()))
    }

    private fun addNewUser(p0: MutableIterable<DataSnapshot>) {
        p0.forEach {
            val playerStatus = it.getValue(PlayerStatus::class.java)
            playerIndex.add(it.key)
            playerList.add(playerStatus)
            adapter.notifyItemInserted(playerIndex.indexOf(it.key))
        }
//        Log.d(GameActivity::class.java.simpleName, "updating*****************************${p0}")

    }

    private fun getSliderBar(){
        val animSwipeLeft = AnimationUtils.loadAnimation(this, R.anim.left_fade_out)
        val animSwipeRight = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_fade_in)
        if (rvPlayerList.visibility == View.VISIBLE){
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
