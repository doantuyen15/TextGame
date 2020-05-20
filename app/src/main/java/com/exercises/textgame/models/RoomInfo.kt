package com.exercises.textgame.models

data class RoomInfo(val hostName: String?=null,
                    val roomTitle: String?=null,
                    val gameType: String,
                    val joinedUser: Any?=null,
                    val userStatus: Any?=null,
                    val attacker: String?="",
                    val message: Any?=null )
{
    constructor() : this("","","",null,null,"",null)
}
const val CHILD_USERSTATUS_KEY = "userStatus"
const val CHILD_JOINEDUSER_KEY = "joinedUser"
const val CHILD_ATTACKER_KEY = "attacker"
const val CHILD_MESSAGE_KEY = "message"
const val CHILD_PLAYERHP_KEY = "playerHp"
const val CHILD_HOSTNAME_KEY = "hostName"
const val CHILD_TITLE_KEY = "roomTitle"