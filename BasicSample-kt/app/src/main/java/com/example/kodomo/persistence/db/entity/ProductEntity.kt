package com.example.kodomo.persistence.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.kodomo.persistence.model.Product

/**
 * @author linxiaotao
 * 2018/12/27 下午5:32
 */
@Entity(tableName = "products")
data class ProductEntity(
        @PrimaryKey var id: Int = 0,
        var name: String = "",
        var description: String = "",
        var price: Int = 0
) : Product {


    @Ignore
    override fun id() = id

    @Ignore
    override fun name() = name

    @Ignore
    override fun description() = description

    @Ignore
    override fun price() = price

}