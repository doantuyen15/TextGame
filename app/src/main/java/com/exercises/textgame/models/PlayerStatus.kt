package com.exercises.textgame.models

import android.net.Uri

// player status holder
data class PlayerStatus(val playerName: String?="Player",
                        val avatarUri: String?,
                        var hp: Long?=100L,
                        var defend: Long?=null,
                        var attack: Long?=null,
                        var surrender: Long?=null
                        )
{
    constructor() : this("Player", null, 0L, 0L, 0L, 0L)
    fun resetPlayerStatus(){
        this.hp = 100L
        this.defend = 0L
        this.attack = 0L
        this.surrender = 0L
    }
}
