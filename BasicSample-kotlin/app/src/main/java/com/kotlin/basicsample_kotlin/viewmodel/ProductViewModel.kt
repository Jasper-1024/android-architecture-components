package com.kotlin.basicsample_kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.basicsample_kotlin.BasicApp
import com.kotlin.basicsample_kotlin.DataRepository
import com.kotlin.basicsample_kotlin.db.entity.CommentEntity
import com.kotlin.basicsample_kotlin.db.entity.ProductEntity

class ProductViewModel(
    application: Application,
    repository: DataRepository,
    ProductId: Int
) : AndroidViewModel(application) {
    private var mProductId: Int = ProductId
    private var mObservableProduct: LiveData<ProductEntity>
    private var mObservableComments: LiveData<List<CommentEntity>>

    init {
        mObservableProduct = repository.loadProduct(mProductId)
        mObservableComments = repository.loadComments(mProductId)
    }

    fun Product() = mObservableProduct
    fun Comments() = mObservableComments

    class Factory(application: Application, productId: Int) :
        ViewModelProvider.NewInstanceFactory() {
        private val mRepository: DataRepository = (application as BasicApp).getRepository()
        private var mApplication = application
        private var mProductId = productId
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProductViewModel(mApplication, mRepository, mProductId) as T
        }
    }
}