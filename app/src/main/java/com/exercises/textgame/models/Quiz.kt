package com.exercises.textgame.models

data class Quiz (
    val language: String = "vn-vn",
    val content: String = "",
    val timeOut: Long = 10000L,
    val answer: Any = "",
    val answer2: Any = "",
    val answer3: Any = ""
)