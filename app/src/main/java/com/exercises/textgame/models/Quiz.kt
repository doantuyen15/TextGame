package com.exercises.textgame.models

data class Quiz (
    val language: String = "vn-vn",
    val content: String = "",
    val timeOut: Long = 10000L,
    val answer: String = "",
    val answer2: String = ""
)