package com.recipia.chat

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatApplication

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

fun main(args: Array<String>) {
	runApplication<ChatApplication>(*args)
}
