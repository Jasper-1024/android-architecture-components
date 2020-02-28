package com.example.kodomo.persistence.db.converter

import androidx.room.TypeConverter
import java.util.*

/**
 * @author linxiaotao
 * 2018/12/27 下午9:09
 */
object DateConverter {

    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(timestamp) }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(date: Date?): Long? = date?.let { date.time }

}