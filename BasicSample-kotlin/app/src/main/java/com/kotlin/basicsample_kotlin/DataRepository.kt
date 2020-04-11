package com.kotlin.basicsample_kotlin

import androidx.lifecycle.MediatorLiveData
import com.kotlin.basicsample_kotlin.db.AppDatabase
import com.kotlin.basicsample_kotlin.db.entity.ProductEntity

class DataRepository(var mdatabase: AppDatabase) {
    private var mObservableProducts: MediatorLiveData<List<ProductEntity>> = MediatorLiveData()

    init {
        mObservableProducts.addSource(mdatabase.productDao().loadAllProducts()) { productEntities ->
            mdatabase.getDatabaseCreated()?.let {
                mObservableProducts.postValue(productEntities)
            }
        }
    }

    companion object {
        private var sInstance: DataRepository? = null
        fun getInstance(database: AppDatabase): DataRepository {
            sInstance?.let { return sInstance as DataRepository }
            return DataRepository(database)
        }
    }

    fun getProducts() = mObservableProducts
    fun loadProduct(productId: Int) = mdatabase.productDao().loadProduct(productId)
    fun loadComments(productId: Int) = mdatabase.commentDao().loadComments(productId)
    fun searchProducts(query: String) = mdatabase.productDao().searchAllProducts(query)
}