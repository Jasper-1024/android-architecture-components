package com.kotlin.basicsample_kotlin.db.entity

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "productsFts")
@Fts4(contentEntity = ProductEntity::class)
data class ProductFtsEntity(
    var name: String = "",
    var description: String = ""
)