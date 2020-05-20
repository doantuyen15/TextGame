package com.exercises.textgame.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exercises.textgame.R
import com.exercises.textgame.models.Message


class ChatLogAdapter(private val context: Context, var data: LinkedHashMap<String, Message?>) : RecyclerView.Adapter<ChatLogAdapter.MessageVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
        return MessageVH(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        val message = data.entries.elementAt(position).value

//        val message = HashMap(messageEntity)
        Log.d(ChatLogAdapter::class.java.simpleName, "Changed*****************************${message}")

        holder.tvMessage.text = message?.message
        holder.tvDisplayName.text = message?.displayName
//        holder.itemView.setOnClickListener {
//            listener.onClick(position)
//        }
    }

//    interface OnClickListener {
//        fun onClick(index: Int)
//    }

    class MessageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvDisplayName: TextView = itemView.findViewById(R.id.tvDisplayName)
    }




}