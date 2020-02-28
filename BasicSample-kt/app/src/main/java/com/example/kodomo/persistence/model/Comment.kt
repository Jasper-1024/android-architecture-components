package com.example.kodomo.persistence.model

import java.util.*

/**
 * @author linxiaotao
 * 2018/12/27 下午6:00
 */
interface Comment {

    fun id(): Int

    fun productId(): Int

    fun text(): String

    fun postedAt(): Date?


}