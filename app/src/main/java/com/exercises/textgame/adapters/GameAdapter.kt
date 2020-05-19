package com.exercises.textgame.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exercises.textgame.R
import com.exercises.textgame.models.PlayerStatus


class GameAdapter(private val context: Context, var data: ArrayList<PlayerStatus?>) : RecyclerView.Adapter<GameAdapter.RoomVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomVH {
        val view = LayoutInflater.from(context).inflate(R.layout.player_item, parent, false)
        return RoomVH(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RoomVH, position: Int) {
        val player = data[position]
        holder.tvPlayer.text = player?.playerName
        holder.processBar.progress = player?.playerHp ?: 0
//        holder.itemView.setOnClickListener {
//            listener.onClick(position)
//        }
    }

//    interface OnClickListener {
//        fun onClick(index: Int)
//    }

    class RoomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlayer: TextView = itemView.findViewById(R.id.tvPlayerName)
        val processBar: ProgressBar = itemView.findViewById(R.id.progressBarPlayer)
    }




}