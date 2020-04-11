package com.kotlin.basicsample_kotlin.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.kotlin.basicsample_kotlin.BasicApp
import com.kotlin.basicsample_kotlin.DataRepository
import com.kotlin.basicsample_kotlin.db.entity.ProductEntity

class ProductListViewModel(
    application: Application,
    var mSavedStateHandler: SavedStateHandle
) : AndroidViewModel(application) {
    private val QUERY_KEY = "QUERY"
    private val mRepository: DataRepository = (application as BasicApp).getRepository()
    private var mProducts: LiveData<List<ProductEntity>>

    init {
        mProducts =
            Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", null)) { query ->
                if (TextUtils.isEmpty(query)) {
                    mRepository.getProducts()
                } else mRepository.searchProducts("*$query*")
            }
    }

    fun setQuery(query: CharSequence) = mSavedStateHandler.set(QUERY_KEY, query)

    fun Products() = mProducts
}