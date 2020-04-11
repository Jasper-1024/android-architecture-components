package com.kotlin.basicsample_kotlin.model

import java.util.*

interface Comment {
    fun Id():Int
    fun ProductID():Int
    fun Text():String
    fun PostedAt():Date?
}