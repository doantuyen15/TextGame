package com.exercises.textgame.models

data class Round (
    val round: Int,
    val quizId: String,
    val syncTimer: Long
)
{
    constructor() : this(0,"", 0L)
}