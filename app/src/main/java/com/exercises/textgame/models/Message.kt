package com.exercises.textgame.models

//Message entity
data class Message(
    val displayName: String?="Player",
    val message: String?=""
)
{
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "displayName" to displayName,
            "message" to message
        )
    }
}
