package com.example.carecircle.model

class Chat(
    var sender: String = "",
    var message: String = "",
    var receiver: String = "",
    var isSeen: Boolean = false,
    var url: String = "",
    var messageId: String = ""
)
