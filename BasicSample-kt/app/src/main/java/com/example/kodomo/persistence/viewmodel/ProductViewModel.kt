package com.example.kodomo.persistence.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kodomo.persistence.BasicApp
import com.example.kodomo.persistence.DataRepository
import com.example.kodomo.persistence.db.entity.CommentEntity
import com.example.kodomo.persistence.db.entity.ProductEntity

/**
 * @author linxiaotao
 * 2018/12/28 下午8:37
 */
class ProductViewModel(application: Application, private val repository: DataRepository, private val productId: Int)
    : AndroidViewModel(application) {

    private val observableProduct: LiveData<ProductEntity>

    val product: ObservableField<ProductEntity> = ObservableField()

    private val observableComments: LiveData<List<CommentEntity>>

    init {
        observableProduct = repository.loadProduct(productId)
        observableComments = repository.loadComments(productId)
    }

    fun setProduct(product: ProductEntity) {
        this.product.set(product)
    }

    fun getObservableProduct() = observableProduct

    fun getObservableComments() = observableComments

    class Factor(val application: Application, val productId: Int) : ViewModelProvider.NewInstanceFactory() {

        val repository: DataRepository

        init {
            repository = (application as BasicApp).repository()
        }

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProductViewModel(application, repository, productId) as T
        }


    }

}