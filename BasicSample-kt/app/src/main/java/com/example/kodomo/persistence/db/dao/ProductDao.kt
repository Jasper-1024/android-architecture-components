package com.example.kodomo.persistence.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kodomo.persistence.db.entity.ProductEntity

/**
 * @author linxiaotao
 * 2018/12/27 下午8:01
 */
@Dao
interface ProductDao {

    @Query("select * from products")
    fun loadAllProducts(): LiveData<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ProductEntity>)

    @Query("select * from products where id = :productId")
    fun loadProduct(productId: Int): LiveData<ProductEntity>

    @Query("select * from products where id = :productId")
    fun loadProductSync(productId: Int): ProductEntity

    @Query("select products.* from products join productFts on (products.id = productFts.rowid) "
            + "where productFts match :query")
    fun searchAllProducts(query: String): LiveData<List<ProductEntity>>
}