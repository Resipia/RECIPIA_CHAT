package com.recipia.chat.domain

/**
 * 유저 정보를 가지고있어야함
 */
data class Member(
        val memberId: Long,
        val nickname: String
)