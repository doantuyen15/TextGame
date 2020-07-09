package com.exercises.textgame.models

// player status holder
data class PlayerStatus(val playerName: String?="Player",
                        var hp: Long?=100L,
                        var defend: Long?=null,
                        var attack: Long?=null,
                        var surrender: Long?=null)
{
    fun resetPlayerStatus(){
        this.hp = 100L
        this.defend = 0L
        this.attack = 0L
        this.surrender = 0L
    }
}
