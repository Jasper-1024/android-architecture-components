package com.example.kodomo.persistence.model

/**
 * @author linxiaotao
 * 2018/12/24 下午4:36
 */
interface Product {

    fun id(): Int

    fun name(): String

    fun description(): String

    fun price(): Int
}