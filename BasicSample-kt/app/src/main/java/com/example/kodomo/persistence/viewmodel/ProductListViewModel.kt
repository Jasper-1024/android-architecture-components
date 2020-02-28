package com.example.kodomo.persistence.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import com.example.kodomo.persistence.BasicApp
import com.example.kodomo.persistence.DataRepository
import com.example.kodomo.persistence.db.entity.ProductEntity

/**
 * @author linxiaotao
 * 2018/12/27 下午4:25
 */
class ProductListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository
    private val observableProducts: MediatorLiveData<List<ProductEntity>>

    init {
        observableProducts = MediatorLiveData()
        observableProducts.value = null

        repository = (application as BasicApp).repository()
        val products = repository.getProducts()

        observableProducts.addSource(
                products,
                observableProducts::setValue
        )
    }

    fun products() = observableProducts

    fun searchProducts(query: String) = repository.searchProducts(query)

}