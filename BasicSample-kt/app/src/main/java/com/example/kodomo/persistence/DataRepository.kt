package com.example.kodomo.persistence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.kodomo.persistence.db.AppDatabase
import com.example.kodomo.persistence.db.entity.ProductEntity

/**
 * @author linxiaotao
 * 2018/12/27 下午4:27
 */
class DataRepository private constructor(private val database: AppDatabase) {

    private val observableProducts: MediatorLiveData<List<ProductEntity>>

    init {
        observableProducts = MediatorLiveData()

        observableProducts.addSource(database.productDao().loadAllProducts()) { products ->
            database.getDatabaseCreated().value?.let {
                observableProducts.postValue(products)
            }
        }
    }

    companion object : SingletonHolder<DataRepository, AppDatabase>(::DataRepository)

    fun getProducts(): LiveData<List<ProductEntity>> = observableProducts

    fun loadProduct(productId: Int) = database.productDao().loadProduct(productId)

    fun loadComments(productId: Int) = database.commentDao().loadComments(productId)

    fun searchProducts(query: String) = database.productDao().searchAllProducts(query)

}