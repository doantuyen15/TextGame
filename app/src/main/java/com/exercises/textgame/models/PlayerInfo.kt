package com.exercises.textgame.models

class PlayerInfo (
    val userName: String,
    val uri: String?
){
    constructor() : this("PlayerName", null)
}
