package com.example.kodomo.persistence.db.entity

import androidx.room.Entity
import androidx.room.Fts4

/**
 * @author linxiaotao
 * 2018/12/27 下午6:13
 */
@Entity(tableName = "productFts")
@Fts4(contentEntity = ProductEntity::class)
data class ProductFtsEntity(
        var name: String = "",
        var description: String = ""
) {

}