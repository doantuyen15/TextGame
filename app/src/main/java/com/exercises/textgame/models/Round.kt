package com.exercises.textgame.models

data class Round (
    val round: Int,
    val quizId: String
)
{
    constructor() : this(0,"")
}