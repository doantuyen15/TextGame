package com.exercises.textgame.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exercises.textgame.R
import com.exercises.textgame.models.RoomInfo


class LobbyAdapter(private val context: Context, var data: ArrayList<RoomInfo>, private val listener: OnClickListener) : RecyclerView.Adapter<LobbyAdapter.LobbyVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyVH {
        val view = LayoutInflater.from(context).inflate(R.layout.room_item, parent, false)
        return LobbyVH(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LobbyVH, position: Int) {
        val room = data[position]
        if (room.roomStatus == "start") {
            holder.title.setTextColor(Color.parseColor("#37EE00A7"))
        }
        holder.title.text = room.roomTitle
        holder.type.text = room.gameType
        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
    }

    interface OnClickListener {
        fun onClick(index: Int)
    }

    class LobbyVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvRoomTitle)
        val type: TextView = itemView.findViewById(R.id.tvRoomType)
    }




}