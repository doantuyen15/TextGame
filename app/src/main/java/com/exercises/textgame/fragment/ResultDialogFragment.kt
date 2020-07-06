package com.exercises.textgame.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.exercises.textgame.R
import com.exercises.textgame.models.PlayerStatus
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.layout_result_dialog.*
import kotlinx.android.synthetic.main.result_item.view.*

class ResultDialogFragment(private val data: ArrayList<PlayerStatus?>, private val mListener: DetachDialogListener): DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogSlideAnim)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.layout_result_dialog, container, false)
        val buttonExit = rootView.findViewById<Button>(R.id.btn_result_exit)
        val buttonPlay = rootView.findViewById<Button>(R.id.btn_result_playagain)
        buttonExit.setOnClickListener {
            dialog?.dismiss()
            mListener.onDetachDialog()
        }
        buttonPlay.setOnClickListener {
            dialog?.dismiss()
            mListener.onRestart()
        }
        return rootView
    }

    override fun onStart() {
        super.onStart()
        val width  = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.setCancelable(false)
        val adapter = GroupAdapter<ViewHolder>()
        data.onEach {
            adapter.add(ResultItem(it))
        }
        rv_result_list.adapter = adapter
    }

    class ResultItem(private val result: PlayerStatus?): Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.result_item
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            if (position == 0){
                viewHolder.itemView.lav_result_medal.visibility = View.VISIBLE
            }
            viewHolder.itemView.tv_result_player.text = result?.playerName
            viewHolder.itemView.tv_result_attack.text = result?.attack.toString()
            viewHolder.itemView.tv_result_defend.text = result?.defend.toString()
            viewHolder.itemView.tv_result_surrender.text = result?.surrender.toString()
        }

    }

    interface DetachDialogListener {
        fun onDetachDialog()
        fun onRestart()
    }
}