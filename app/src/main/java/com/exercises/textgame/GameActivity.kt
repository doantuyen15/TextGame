package com.exercises.textgame

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.*
import android.widget.ProgressBar
import android.widget.TextView
import com.exercises.textgame.adapters.ChatLogAdapter
import com.exercises.textgame.adapters.GameAdapter
import com.exercises.textgame.models.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.layout_loading_dialog.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.system.exitProcess

class GameActivity : BaseActivity() {

    private lateinit var adapter: GameAdapter
    private lateinit var adapterChatLog : ChatLogAdapter
    private val playerListStatus = ArrayList<PlayerStatus?>()
    private var playerIndex = ArrayList<String?>()
    private var playerList = HashMap<String,String>()
    private val chatLogs = LinkedHashMap<String, Message?>()
    private val keyLogs = ArrayList<String?>()
    private var uid: String? = ""
    private var joinedRoomKey: String? = ""
    private var mapAnswer = ArrayList<String?>()
    private var resultTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val data = intent.extras
        joinedRoomKey = data?.getString(ROOM_INFO_KEY)
        uid = data?.getString(USER_UID_KEY)
        if(data?.getString(CHILD_HOSTNAME_KEY) != null){
            btnStartGame.visibility = View.VISIBLE
        }
        fetchCurrentRoomInfo()
        getConnectionState()
        adapter = GameAdapter(this, playerListStatus)
        adapterChatLog = ChatLogAdapter(this, chatLogs)
        rvPlayerList.adapter = adapter
        rvChatLog.adapter = adapterChatLog

        // open/close slider bar Player list status
        sliderButton.setOnClickListener{
            sliderButton.isEnabled = false
            getSliderBar()
        }
        //Send message
        validateMessage()
        btnSendMessage.setOnClickListener {
            if (uid != null) {
                sendMessage(edtMessage.text.toString())
            }
        }
        //get Speech
        btnMic.setOnClickListener {
            getSpeech(LANGUAGE_EN_KEY)
        }
        //Start Game
        btnStartGame.setOnClickListener {
            sendCommand("start")
        }
    }

    private fun getConnectionState() {
//        val connectionRef = roomRef.child(joinedRoomKey!!).child("connections")
        val dialog = AlertDialog.Builder(this@GameActivity)
            .setCancelable(false)
            .setView(R.layout.layout_loading_dialog)
            .setNegativeButton("Cancel", DialogInterface.OnClickListener{_,_ ->
                finish()
            })
            .create()
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (!connected) {
                    dialog.show()
                    Log.d("getConnectionState", "disconnected to server")
                } else{
                    Log.d("getConnectionState", "connected to server")
                    if(dialog != null && dialog.isShowing){
                        dialog.dismiss()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("getConnectionState", "Listener was cancelled with error: $error")
            }
        })
    }

    private fun getSpeech(languageKey: String) {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageKey)
//        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something")
        try{
            startActivityForResult(mIntent, REQUEST_SPEECH_CODE)
        }
        catch (e: Exception){
            Log.d(GameActivity::class.java.simpleName, "RecordSpeechStart*****************************${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SPEECH_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            result?.let {
                sendMessage(it[0])
            }
            Log.d(
                GameActivity::class.java.simpleName,
                "RecordSpeechResult*****************************${result?.let{it[0]}}"
            )
        }
    }
    private fun validateMessage() {
        edtMessage.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                btnSendMessage.isEnabled = false
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnSendMessage.isEnabled = s.toString().trim().isNotBlank()
            }
        })
    }

    private fun sendMessage(message: String) {
        var keyLog = ""
        val chatLog = HashMap<String, Message?>()
        if (!mapAnswer.isNullOrEmpty()){
            if(mapAnswer.contains(message.toLowerCase(Locale.getDefault()))) {
                sendCommand("attack")
            }
        }
        joinedRoomKey?.let{
            try{
                keyLog = dbGetRefRoom(it).push().key!!
                keyLogs.add(keyLog)
            } catch (e : Exception){
                Log.e(GameActivity::class.java.simpleName, "Changed*****************************${e.message}")
            }
            chatLog[keyLog] = Message(playerList[uid].toString(), message)
            chatLogs.putAll(chatLog)
            edtMessage.text.clear()
            dbGetRefRoom(it)
                .child(CHILD_MESSAGE_KEY)
                .updateChildren(chatLog as Map<String, Any>)
                .addOnSuccessListener {
                    adapterChatLog.notifyItemInserted(adapterChatLog.itemCount)
                    rvChatLog.scrollToPosition(adapterChatLog.itemCount - 1)
                }
        }
    }

    private fun sendCommand(cmd: String) {
        val command = HashMap<String, Any>()
        when (cmd){
            "attack" -> {
                command["attack$joinedRoomKey"] = resultTime
                commandRef.updateChildren(command)
            }
            "start" -> {
                command["start$joinedRoomKey"] = playerIndex.zip(playerListStatus).toMap()
                roomRef.child(joinedRoomKey!!).child(CHILD_ROOMSTATUS_KEY).setValue("playing")
                commandRef.updateChildren(command)
            }
        }
    }

    private fun fetchCurrentRoomInfo(){
        joinedRoomKey?.let{
            dbGetRefRoom(it).addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("fetchCurrentRoomInfo", "Cancelled with error: ${p0.message}")
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
                    if(p0.key == CHILD_USERSTATUS_KEY){
                        updateUserStatus()
                    }
                    if(p0.key == CHILD_MESSAGE_KEY){
                        updateLastMessage(p0.children.last())
                    }
                    if(p0.key == CHILD_ROUND_KEY){
                        p0.getValue(Round::class.java)?.let { value -> getQuiz(value) }
                    }
                    Log.d("fetchCurrentRoomInfo", "Changed*****************************${p0.key}")
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    if (p0.key == CHILD_USERSTATUS_KEY){
                        refreshAdapterStatus(p0.children)
//                        Log.d(GameActivity::class.java.simpleName, "Added*****************************${p0.key}")
                    }
                    if (p0.key == CHILD_JOINEDUSER_KEY) {
                        fetchUser(p0.children)
                    }
                    if(p0.key == CHILD_ROUND_KEY){
                        p0.getValue(Round::class.java)?.let { id -> getQuiz(id) }
                        Log.d("fetchCurrentRoomInfo", "Changed*****************************${p0.key}")
                    }
                    if(p0.key == CHILD_MESSAGE_KEY){
                        updateLastMessage(p0.children.last())
                    }
//                    Log.d(GameActivity::class.java.simpleName, "Added*****************************${p0.key}")
                }

                override fun onChildRemoved(p0: DataSnapshot) {
//                    TODO("Not yet implemented")
                }

            })
        }
//        adapter.add(RoomHolder(playerList))
    }

    private fun getQuiz(round: Round) {
        mapAnswer.clear()
        getDbQuiz("quiz").document(round.quizId)
            .get()
            .addOnSuccessListener {
                val quiz = it.toObject(Quiz::class.java)
                mapAnswer.add(quiz?.answer?.toLowerCase(Locale.getDefault()))
                quiz?.answer2?.let{ ans -> mapAnswer.add(ans.toLowerCase(Locale.getDefault())) }
                Log.d("get quiz", "${quiz?.timeOut}")
                if(quiz != null) showQuiz(round.round, quiz)
            }
            .addOnFailureListener {
                Log.d("get quiz", "failed with error: ${it.message}")
                retryToConnect()
            }
    }

    private fun retryToConnect() {
//        TODO("Not yet implemented")
    }

    private fun showQuiz(r: Int, quiz: Quiz) {
        resultTime = 0L
        cv_quiz.visibility = View.VISIBLE
        val round = findViewById<TextView>(R.id.tvRound)
        val content = findViewById<TextView>(R.id.tvQuizTitle)
        val counter = findViewById<ProgressBar>(R.id.quizProgressBar)
        val timeOut = quiz.timeOut
        counter.max = timeOut.toInt()
        round.text = r.toString()
        content.text = quiz.content
        val timer = object : CountDownTimer(timeOut, 100) {
            override fun onFinish() {
                //
            }

            override fun onTick(p0: Long) {
                resultTime = timeOut - p0
                counter.progress = p0.toInt()
                Log.d("show quiz", "${counter.progress}")
            }

        }
        timer.start()
    }

    private fun updateLastMessage(newMessage: DataSnapshot) {
        val newKey = newMessage.key
        if (!keyLogs.contains(newKey)) {
            keyLogs.add(newKey)
            chatLogs[newKey.toString()] = newMessage.getValue(Message::class.java)
            adapterChatLog.notifyItemInserted(keyLogs.indexOf(newKey))
            rvChatLog.scrollToPosition(adapterChatLog.itemCount - 1)
        }
    }

    private fun refreshAdapterStatus(playerStatus: MutableIterable<DataSnapshot>, isUpdate: Boolean=false){
        //key=uid
        if(isUpdate){
            playerListStatus.clear()
            playerIndex.clear()
        }
        playerStatus.forEach {
            if(isUpdate){
                playerIndex.add(it.key)
//                Log.d(GameActivity::class.java.simpleName, "Changed*****************************$playerIndex")
            }
//            val index = playerIndex.indexOf(it.key)
            playerListStatus.add(it.getValue(PlayerStatus::class.java))
        }
        getSliderBar(true)
        adapter.notifyDataSetChanged()
    }

    private fun updateUserStatus() {
        try {
            dbGetRefRoom(joinedRoomKey!!)
                .child(CHILD_USERSTATUS_KEY)
                .orderByChild(CHILD_PLAYERHP_KEY)
                .limitToLast(101)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
//
                    }

                    override fun onDataChange(p0: DataSnapshot) {
//                        fetchUser(p0.children, true)
                        refreshAdapterStatus(p0.children, true)
                    }
                })
        } catch (e: Exception) {
            Log.e("updateUserStatus","updateError*****************************${e}"
            )
        }
    }

    private fun fetchUser(newJoinedPlayer: MutableIterable<DataSnapshot>, isSorted: Boolean=false) {
        newJoinedPlayer.forEach {
            val newKey = it.key.toString()
            if (!playerList.containsKey(newKey)) {
                playerList[newKey] = it.value as String
                playerIndex.add(newKey)
            }
        }
    }

    private fun getSliderBar(isRefresh: Boolean = false){
        val animSwipeLeft = AnimationUtils.loadAnimation(this, R.anim.left_fade_out)
        val animSwipeRight = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_fade_in)
        if (rvPlayerList.visibility == View.VISIBLE && !isRefresh){
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
}