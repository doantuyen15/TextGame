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
import com.google.firebase.firestore.Source
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
    private var isStart = false

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

        adapter = GameAdapter(this, playerListStatus, playerLongClickListener)
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
            edtMessage.text.clear()
        }
        //get Speech
        btnMic.setOnClickListener {
            getSpeech(LANGUAGE_EN_KEY)
        }
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
                sendMessage(it.first())
            }
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
        keyLog = dbGetRefRoom(joinedRoomKey).push().key!!
        keyLogs.add(keyLog)
        val mes = Message(playerList[uid].toString(), message)
        chatLogs.add(mes)
        dbGetRefRoom(joinedRoomKey)
            .child(CHILD_MESSAGE_KEY)
            .child(keyLog)
            .setValue(mes)

        adapterChatLog.notifyItemInserted(adapterChatLog.itemCount)
        rvChatLog.scrollToPosition(adapterChatLog.itemCount - 1)

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
    }

    private fun sendCommand(cmd: String, index: Int?=null) {
        when (cmd){
            COMMAND_ATTACK_KEY -> {
                val timestamp = (System.currentTimeMillis() - roundStartStamp).toString()
                commandRef.child(joinedRoomKey)
                    .child(timestamp)
                    .setValue(uid)
            }
            COMMAND_START_KEY -> {
                isStart = true
                commandRef.child(joinedRoomKey)
                    .setValue("start")
            }
            COMMAND_QUIT_KEY -> {
                if(playerList.count() > 1){
                    playerList.remove(uid)
                    if (isHost){
                        val newHost = playerList.entries.first().value
                        dbGetRefRoom(joinedRoomKey).apply {
                            child(CHILD_HOSTNAME_KEY)
                                .setValue(newHost)
                            child(CHILD_MESSAGE_KEY)
                                .push()
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
                playerListStatus.onEach {
                    it?.resetPlayerStatus()
                }
                adapter.notifyDataSetChanged()
            }
            COMMAND_KICK_KEY -> {
                if (index != null) {
                    val playerName = playerListStatus[index]?.playerName
                    val playerId = playerList.filterValues { it == playerName }.keys.first()
                    playerList.remove(playerId)
                    playerListStatus.removeAt(index)
                    playerIndex.remove(playerId)
                    roomRef.apply {
                        child(joinedRoomKey).child(CHILD_JOINEDUSER_KEY).child(playerId)
                            .setValue(null)
                        child(CHILD_LISTROOMS_KEY).child(joinedRoomKey).child(CHILD_JOINEDUSER_KEY)
                            .child(playerId)
                            .setValue(null)
//                        commandRef.child(joinedRoomKey)
//                            .setValue("$COMMAND_KICK_KEY$playerId")
                    }
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
                updateLastMessage(p0.children.drop(keyLogs.count()))
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
                updateLastMessage(p0.children.drop(keyLogs.count()))
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
        isStart = false
        val mDialogListener = object: ResultDialogFragment.DetachDialogListener {
            override fun onDetachDialog() {
                sendCommand("quit")
                setResult(RESULT_CANCELED)
                finish()
            }

            override fun onRestart() {
                sendCommand(COMMAND_STAY_KEY)
            }
        }
        val resultDialog = ResultDialogFragment(playerListStatus, mDialogListener)
        resultDialog.show(supportFragmentManager, "result")
    }

    private fun fetchCurrentRoomInfo() {
        dbGetRefRoom(joinedRoomKey).addChildEventListener(roomEventListener)
    }

    private fun getQuiz(round: Round) {
        mapAnswer.clear()

        getDbQuiz("quiz").document(round.quizId)
            .get(Source.CACHE)
            .addOnSuccessListener {
                val source = if (it.metadata.isFromCache)
                    "local cache"
                else
                    "server"
                Log.d("getQuiz***********", "Data fetched from $source")
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

    private fun updateLastMessage(newMessage: List<DataSnapshot>) {
        newMessage.forEach { newMess ->
            val newKey = newMess.key
            if (!keyLogs.contains(newKey)) {
                keyLogs.add(newKey)
                val message = newMess.getValue(Message::class.java)
                message?.let { chatLogs.add(it) }
                adapterChatLog.notifyItemInserted(keyLogs.indexOf(newKey))
                rvChatLog.scrollToPosition(adapterChatLog.itemCount - 1)
            }
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
        playerIndex.clear()
        playerListStatus.clear()
        newJoinedPlayer.forEach {
            val id = it.key.toString()

            val playerName = it.value.toString()
            playerList[id] = playerName
            playerIndex.add(id)
            playerListStatus.add(PlayerStatus(playerName))
        }
        adapter.notifyDataSetChanged()
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

    private val playerLongClickListener = object: GameAdapter.OnClickPlayerListener {
        override fun onLongClickPlayer(index: Int) {
            if(isHost && index != playerIndex.indexOf(uid) && !isStart){
                val builder = AlertDialog.Builder(this@GameActivity)
                    .setMessage("Kick this player?")
                    .setPositiveButton("YES") { _, _ ->
                        sendCommand(COMMAND_KICK_KEY, index)
                    }
                    .setNegativeButton("NO") { dialog, _ -> dialog?.dismiss() }
                val dialog = builder.create();
                dialog.show()
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
