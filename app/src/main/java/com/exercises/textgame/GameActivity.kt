package com.exercises.textgame

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.exercises.textgame.adapters.ChatLogAdapter
import com.exercises.textgame.adapters.GameAdapter
import com.exercises.textgame.fragment.AlertDialogFragment
import com.exercises.textgame.fragment.ResultDialogFragment
import com.exercises.textgame.models.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GameActivity : BaseActivity() {
    private lateinit var adapter: GameAdapter
    private lateinit var adapterChatLog : ChatLogAdapter
    private val playerListStatus = ArrayList<PlayerStatus?>()
    private var playerIndex = ArrayList<String?>()
    private var playerList = HashMap<String,String>()
    private val chatLogs = ArrayList<Message>()
    private val keyLogs = ArrayList<String?>()
    private var uid: String = ""
    private var userName: String = ""
    private var joinedRoomKey: String = ""
    private var mapAnswer = ArrayList<String?>()
    private var roundStartStamp = 0L
    private var timeOut = 0L
    private var isHost = false
    private var isAnswer = false
    private var reconnect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setDialogAlert(dialogListener)
        checkNetworkConnectivity()

        val data = intent.extras
        joinedRoomKey = data?.getString(ROOM_INFO_KEY)!!
        uid = data.getString(USER_UID_KEY)!!
        userName = data.getString(USER_USERNAME_KEY)!!

        fetchCurrentRoomInfo()
//        getConnectionState(this)

        adapter = GameAdapter(this, playerListStatus)
        adapterChatLog = ChatLogAdapter(this, chatLogs)
        rvPlayerList.adapter = adapter
        rvChatLog.adapter = adapterChatLog

        // open/close slider bar Player list status
        sliderButton.setOnClickListener{
            sliderButton.isEnabled = false
            updatePlayerStatusBar()
        }
        //Send message
        validateMessage()
        btnSendMessage.setOnClickListener {
                sendMessage(edtMessage.text.toString())
        }
        //get Speech
        btnMic.setOnClickListener {
            getSpeech(LANGUAGE_EN_KEY)
        }
        //Start Game
//        btnStartGame.setOnClickListener {
//            btnStartGame.isEnabled = false
//            btnStartGame.visibility = View.GONE
//            sendCommand("start")
//        }
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
        if (message == ".start" && isHost) {
            sendCommand("start")
        }
        if (!mapAnswer.isNullOrEmpty() && !isAnswer) {
            isAnswer = true
            val ans = message
                .replace(".", "")
                .replace(" ", "")
                .toLowerCase(Locale.getDefault())
            if (mapAnswer.contains(ans)) {
                sendCommand("attack")
            }
        }
        joinedRoomKey.let{
            try{
                keyLog = dbGetRefRoom(it).push().key!!
                keyLogs.add(keyLog)
            } catch (e : Exception){
                Log.e(GameActivity::class.java.simpleName, "Changed*****************************${e.message}")
            }
            val mes = Message(playerList[uid].toString(), message)
            chatLog[keyLog] = mes
            chatLogs.add(mes)
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
        when (cmd){
            COMMAND_ATTACK_KEY -> {
                val timestamp = (System.currentTimeMillis() - roundStartStamp).toString()
                commandRef.child(joinedRoomKey)
                    .child(timestamp)
                    .setValue(uid)
            }
            COMMAND_START_KEY -> {
                commandRef.child(joinedRoomKey)
                    .setValue("start")
            }
            COMMAND_QUIT_KEY -> {
                if(playerList.count() > 1){
                    playerList.remove(uid)
                    if (isHost){
                        val newHost = playerList.entries.first().key
                        dbGetRefRoom(joinedRoomKey).apply {
                            child(CHILD_HOSTNAME_KEY)
                                .setValue(newHost)
                            child(CHILD_MESSAGE_KEY)
                                .setValue(Message("Bot", "$userName has left\nNew host is $newHost"))
                        }
                    }
                    commandRef.child(joinedRoomKey).child(CHILD_JOINEDUSER_KEY)
                        .child(uid)
                        .setValue(null)
                } else {
                    commandRef.child(joinedRoomKey)
                        .setValue("quit")
                }
            }
            COMMAND_RECONNECTED_KEY -> {
                dbGetRefRoom(joinedRoomKey)
                    .child(CHILD_MESSAGE_KEY)
                    .push()
                    .setValue(Message("Bot", "$userName has reconnected"))
            }
            COMMAND_STAY_KEY -> {
                if(isHost) {
                    commandRef.child(joinedRoomKey)
                        .setValue("restart")
                }
            }
        }
    }

    private val roomEventListener = object: ChildEventListener{
        override fun onCancelled(p0: DatabaseError) {
            Log.d("fetchCurrentRoomInfo", "Cancelled with error: ${p0.message}")
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//                    TODO("Not yet implemented")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            if (p0.key == CHILD_ROOMSTATUS_KEY) {
                when (p0.value.toString()) {
                    "finish" -> {
                        showResult()
                    }
                    "checking" -> {
                        dbGetRefRoom(joinedRoomKey).child(CHILD_ROOMSTATUS_KEY).child("checking")
                            .child(uid)
                            .setValue("online")
                    }
                }
            }
            if (p0.key == CHILD_JOINEDUSER_KEY) {
                fetchPlayer(p0.children)
            }
            if (p0.key == CHILD_PLAYERSTATUS_KEY) {
                updatePlayerStatus(p0.children)
            }
            if (p0.key == CHILD_MESSAGE_KEY) {
                updateLastMessage(p0.children.last())
            }
            if (p0.key == CHILD_ROUND_KEY) {
                isAnswer = false
                p0.getValue(Round::class.java)?.let { value -> getQuiz(value) }
            }
//            Log.d("fetchCurrentRoomInfo", "Changed*****************************${p0.key}")
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//            Log.d("fetchCurrentRoomInfo", "Added*****************************${p0.key}")
            if (p0.key == CHILD_HOSTNAME_KEY && p0.value.toString() == userName){
//                btnStartGame.isEnabled = true
//                btnStartGame.visibility = View.VISIBLE
                isHost = true
            }
            if (p0.key == CHILD_ROOMSTATUS_KEY){
                adapter.notifyGameStart()
            }
            if (p0.key == CHILD_PLAYERSTATUS_KEY){
                updatePlayerStatus(p0.children)
//                        Log.d(GameActivity::class.java.simpleName, "Added*****************************${p0.key}")
            }
            if (p0.key == CHILD_JOINEDUSER_KEY) {
                fetchPlayer(p0.children)
            }
            if(p0.key == CHILD_ROUND_KEY){
                isAnswer = false
                p0.getValue(Round::class.java)?.let { id -> getQuiz(id) }
//                Log.d("fetchCurrentRoomInfo", "Changed*****************************${p0.key}")
            }
            if(p0.key == CHILD_MESSAGE_KEY){
                updateLastMessage(p0.children.last())
            }
            if(p0.key == CHILD_ATTACKER_KEY){
                if(p0.value.toString() == uid){
//                    attackOtherUser()
                }
            }
            if(p0.key == CHILD_DEFENDER_KEY){
//                        updateUserStatus(p0.child(CHILD_JOINEDUSER_KEY))
            }
//                    Log.d(GameActivity::class.java.simpleName, "Added*****************************${p0.key}")
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            if (p0.key == CHILD_ROOMSTATUS_KEY){
                adapter.notifyGameEnd()
            }
        }
    }

    private fun showResult() {
        val mDialogListener = object: ResultDialogFragment.DetachDialogListener {
            override fun onDetachDialog() {
                sendCommand("quit")
                setResult(RESULT_CANCELED)
                finish()
            }

            override fun onRestart() {
                playerListStatus.clear()
                sendCommand(COMMAND_STAY_KEY)
                adapter.notifyDataSetChanged()
            }
        }
        val resultDialog = ResultDialogFragment(playerListStatus, mDialogListener)
        resultDialog.show(supportFragmentManager, "result")
    }

    private fun fetchCurrentRoomInfo() {
        dbGetRefRoom(joinedRoomKey).addChildEventListener(roomEventListener)
//        adapter.add(RoomHolder(playerList))
    }

    private fun getQuiz(round: Round) {
        mapAnswer.clear()
        getDbQuiz("quiz").document(round.quizId)
            .get()
            .addOnSuccessListener {
                val quiz = it.toObject(Quiz::class.java)
                mapAnswer.add(quiz?.answer
                    ?.replace(".", "")
                    ?.replace(" ", "")
                    ?.toLowerCase(Locale.getDefault()))
                quiz?.answer2?.let{ ans -> mapAnswer.add(ans
                    .replace(".", "")
                    .replace(" ", "")
                    .toLowerCase(Locale.getDefault())) }
                Log.d("get quiz", "${quiz?.timeOut}")
                if(quiz != null) showQuiz(round.round, round.syncTimer, quiz)
            }
            .addOnFailureListener {
                Log.d("get quiz", "failed with error: ${it.message}")
//                retryToConnect()
            }
    }

    private fun startCountDown(timeOut: Long, syncTime: Long) {
        val counter = findViewById<ProgressBar>(R.id.quizProgressBar)
        val progress = (syncTime.toFloat().div(timeOut)*1000).toInt()
//        counter.progress = progress
        val progressAnimator = ObjectAnimator.ofInt(counter, "progress", progress, 0)
        progressAnimator.duration = syncTime
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.start()
    }

    private fun showQuiz(r: Int, syncTime: Long, quiz: Quiz) {
        roundStartStamp = System.currentTimeMillis()
        cv_quiz.visibility = View.VISIBLE
        val round = findViewById<TextView>(R.id.tvRound)
        val content = findViewById<TextView>(R.id.tvQuizTitle)
        timeOut = quiz.timeOut
        round.text = r.toString()
        content.text = quiz.content
        var timeStamp = timeOut - (roundStartStamp - syncTime)
        if (timeStamp < 0L) timeStamp = 0L
        startCountDown(timeOut, timeStamp)
    }

    private fun updateLastMessage(newMessage: DataSnapshot) {
        val newKey = newMessage.key
        if (!keyLogs.contains(newKey)) {
            keyLogs.add(newKey)
            val message = newMessage.getValue(Message::class.java)
            message?.let { chatLogs.add(it) }
            adapterChatLog.notifyItemInserted(keyLogs.indexOf(newKey))
            rvChatLog.scrollToPosition(adapterChatLog.itemCount - 1)
        }
    }

    private fun updatePlayerStatus(playerStatus: MutableIterable<DataSnapshot>){
        //key=uid
        playerListStatus.clear()
        playerStatus.forEach {
            playerListStatus.add(it.getValue(PlayerStatus::class.java))
        }
        updatePlayerStatusBar(true)
        adapter.notifyDataSetChanged()
    }

    private fun fetchPlayer(newJoinedPlayer: MutableIterable<DataSnapshot>) {
        newJoinedPlayer.forEach {
            val id = it.key.toString()
            val playerName = it.value.toString()
            if (!playerList.containsKey(id)) {
                playerList[id] = playerName
                playerIndex.add(id)
                playerListStatus.add(PlayerStatus(playerName))
                adapter.notifyDataSetChanged()
            } else {
                playerList.remove(id)
                playerIndex.remove(id)
            }
        }
    }

    private fun updatePlayerStatusBar(isRefresh: Boolean = false){
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

    private val dialogListener = object: AlertDialogFragment.DetachDialogListener {
        override fun onDetachDialog() {
            setResult(RESULT_CANCELED)
            finish()
        }

        override fun onReconnected() {
            sendCommand(COMMAND_RECONNECTED_KEY)
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseDatabase.getInstance().purgeOutstandingWrites()
//        reconnectToServer()
//        reconnect = true
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        dbGetRefRoom(joinedRoomKey).removeEventListener(roomEventListener)
        removeNetworkListener()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
            .setMessage("Are you sure to quit?")
            .setNegativeButton("Yes") { _, _ ->
                sendCommand("quit")
                setResult(Activity.RESULT_CANCELED)
                super.onBackPressed()
                finish()
            }
            .setPositiveButton("Dismiss") { dialog, _ -> dialog?.dismiss() }
        val dialog = builder.create();
        dialog.show()
    }

}
