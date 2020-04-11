package com.kotlin.basicsample_kotlin.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kotlin.basicsample_kotlin.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var price: Int = 0
) : Product {
    init {
    }

    constructor(product: Product) : this() {
        id = product.Id()
        name = product.Name()
        description = product.Description()
        price = product.Price()
    }
    @Ignore
    override fun Id(): Int = Id()
    @Ignore
    override fun Name(): String = Name()
    @Ignore
    override fun Description(): String = Description()
    @Ignore
    override fun Price(): Int = Price()
}