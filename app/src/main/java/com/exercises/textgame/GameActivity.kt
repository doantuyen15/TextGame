package com.exercises.textgame

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.player_item.view.*


class GameActivity : BaseActivity() {

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        addDummyPlayer()
        rvPlayerList.adapter = adapter
        sliderButton.setOnClickListener{
            sliderButton.isEnabled = false
            getSliderBar()
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

    class RoomHolder(private val ctx: Context, private val playerInfo: PlayerInfo): Item<ViewHolder>(){

        override fun getLayout(): Int {
            return R.layout.player_item
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.tvPlayerName.text = playerInfo.playerName
            viewHolder.itemView.progressBarPlayer.progress = playerInfo.playerHp!!
        }

    }
}
