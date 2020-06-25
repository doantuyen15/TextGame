package com.exercises.textgame.models

// player status holder
data class PlayerStatus(val playerName: String?="Player",
                        val hp: Long?=100L,
                        val defend: Long?=null,
                        val attack: Long?=null,
                        val surrender: Long?=null)
{
    constructor() : this ("",0L,null,null,null)
}
