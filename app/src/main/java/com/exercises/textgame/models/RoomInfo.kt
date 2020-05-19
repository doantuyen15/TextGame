package com.exercises.textgame.models

data class RoomInfo(val hostName: String?=null,
                    val roomTitle: String?=null,
                    val gameType: String,
                    val joinedUser: Any?=null,
                    val userStatus: Any?=null,
                    val attacker: String?="" )
{
    constructor() : this("","","",null,null,"")
}
const val CHILD_USERSTATUS_KEY = "userStatus"
const val CHILD_JOINEDUSER_KEY = "joinedUser"
const val CHILD_ATTACKER_KEY = "attacker"