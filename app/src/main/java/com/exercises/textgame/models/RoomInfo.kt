package com.exercises.textgame.models

data class RoomInfo(val hostName: String?=null,
                    val roomTitle: String?=null,
                    val gameType: String,
                    val joinedUser: HashMap<String, String>?=null,
                    var roomStatus: String?="create"
                    )
{
    constructor() : this("","","",null,null)
}
const val CHILD_PLAYERSTATUS_KEY = "playerStatus"
const val CHILD_JOINEDUSER_KEY = "joinedUser"
const val CHILD_ATTACKER_KEY = "attacker"
const val CHILD_DEFENDER_KEY = "defender"
const val CHILD_MESSAGE_KEY = "message"
const val CHILD_PLAYERHP_KEY = "playerHp"
const val CHILD_HOSTNAME_KEY = "hostName"
const val CHILD_TITLE_KEY = "roomTitle"
const val CHILD_ROUND_KEY = "round"
const val CHILD_ROOMSTATUS_KEY = "roomStatus"