package com.exercises.textgame

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlinx.android.synthetic.main.player_item.view.*

class GameActivity : BaseActivity() {

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val animFadeIn = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)
        addDummyPlayer()

        button.setOnClickListener {
            llPlayerList.visibility = View.VISIBLE
            //rvPlayerList.layoutAnimation = animFadeIn
            rvPlayerList.adapter = adapter
            //showSlideBar()
        }
    }

    private fun addDummyPlayer(){
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
        adapter.add(RoomHolder(this, PlayerInfo()))
    }

//    private fun showSlideBar(){
//        val animFadeIn = AnimationUtils.loadAnimation(this,R.anim.left_fade_in)
//        llPlayerList.startAnimation(animFadeIn)
//        animFadeIn.setAnimationListener(object: Animation.AnimationListener{
//            override fun onAnimationRepeat(animation: Animation?) {
//                //
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                //
//            }
//
//            override fun onAnimationStart(animation: Animation?) {
//                llPlayerList.visibility = View.VISIBLE
//            }
//
//        })
//    }

    class RoomHolder(private val ctx: Context, private val playerInfo: PlayerInfo): Item<ViewHolder>(){

        override fun getLayout(): Int {
            return R.layout.player_item
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val animFadeIn = AnimationUtils.loadAnimation(ctx, R.anim.left_fade_in)
            viewHolder.itemView.container.startAnimation(animFadeIn)
            viewHolder.itemView.tvPlayerName.text = playerInfo.playerName
            viewHolder.itemView.progressBarPlayer.progress = playerInfo.playerHp!!
        }

    }
}
