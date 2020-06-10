package com.exercises.textgame.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exercises.textgame.R
import com.exercises.textgame.models.Message
import com.firebase.ui.auth.data.model.User

private const val VIEW_TYPE_USER_MESSAGE = 1
private const val VIEW_TYPE_BOT_MESSAGE = 2

class ChatLogAdapter(private val context: Context, var data: ArrayList<Message>) : RecyclerView.Adapter<MessageVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        return if(viewType == VIEW_TYPE_USER_MESSAGE) {
            UserMessage(LayoutInflater.from(context).inflate(R.layout.user_chat_item, parent, false))
        } else {
            BotMessage(LayoutInflater.from(context).inflate(R.layout.bot_chat_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = data[position]
        return if (message.displayName == "Bot") VIEW_TYPE_BOT_MESSAGE else VIEW_TYPE_USER_MESSAGE
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        val message = data.elementAt(position)
        holder.bind(message)
//        holder.itemView.setOnClickListener {
//            listener.onClick(position)
//        }
    }

    inner class UserMessage (view: View): MessageVH(view){
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvDisplayName: TextView = itemView.findViewById(R.id.tvDisplayName)

        override fun bind(message: Message) {
            if (!message.message.isNullOrBlank()) {
                tvMessage.text = message.message
                tvDisplayName.text = message.displayName
            }
        }
    }

    inner class BotMessage (view: View): MessageVH(view){
        private val tvBotMessage: TextView = itemView.findViewById(R.id.tvBotMessage)
        private val tvBotName: TextView = itemView.findViewById(R.id.tvBotName)

        override fun bind(message: Message) {
            if (!message.message.isNullOrBlank()) {
                tvBotMessage.text = message.message
                tvBotName.text = message.displayName
            }
        }
    }

//    interface OnClickListener {
//        fun onClick(index: Int)
//    }
}

open class MessageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(message: Message) {}
}