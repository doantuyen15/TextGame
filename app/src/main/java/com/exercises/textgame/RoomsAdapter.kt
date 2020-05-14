package com.exercises.textgame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoomsAdapter(private val context: Context, var data: RoomInfo, private val listener: OnClickListener) : RecyclerView.Adapter<RoomsAdapter.MoviesVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesVH {
        val view = LayoutInflater.from(context).inflate(R.layout.room_item, parent, false)
        return MoviesVH(view)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: MoviesVH, position: Int) {
//        holder.title.text = data.roomTitle
//        holder.overview.text = data.gameType
    }

    interface OnClickListener {
        fun onClick(movie: RoomInfo)
    }

    class MoviesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val title = itemView.findViewById<TextView>(R.id.tvItemTitleNew)
//        val overview = itemView.findViewById<TextView>(R.id.tvItemOverviewNew)
    }




}