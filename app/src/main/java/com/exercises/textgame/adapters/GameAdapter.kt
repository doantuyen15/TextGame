package com.exercises.textgame.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.exercises.textgame.R
import com.exercises.textgame.models.PlayerStatus
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*


class GameAdapter(private val context: Context, var data: ArrayList<PlayerStatus?>, private val listener: OnClickPlayerListener) : RecyclerView.Adapter<GameAdapter.RoomVH>() {
    private var isStart: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomVH {
        val view = LayoutInflater.from(context).inflate(R.layout.player_item, parent, false)
        return RoomVH(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RoomVH, position: Int) {
        val player = data[position]
        Glide.with(context)
            .load(player?.avatarUri)
            .placeholder(R.drawable.googleicon)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.avatar)
        holder.tvPlayer.text = player?.playerName
        if(!isStart){
            holder.processBar.visibility = View.GONE
        } else {
            holder.processBar.visibility = View.VISIBLE
            holder.processBar.progress = (player?.hp)?.toInt() ?: 0
        }
        holder.itemView.setOnLongClickListener {
            listener.onLongClickPlayer(position)
            true
        }
    }

    interface OnClickPlayerListener {
        fun onLongClickPlayer(index: Int)
    }

    fun notifyGameStart() {
        isStart = true
        notifyDataSetChanged()
    }

    fun notifyGameEnd() {
        isStart = false
        notifyDataSetChanged()
    }

    class RoomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlayer: TextView = itemView.findViewById(R.id.tvPlayerName)
        val processBar: ProgressBar = itemView.findViewById(R.id.progressBarPlayer)
        val avatar: ImageView = itemView.findViewById(R.id.cvPlayerAvatar)
    }




}