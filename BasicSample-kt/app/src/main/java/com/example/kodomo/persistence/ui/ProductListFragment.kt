package com.example.kodomo.persistence.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kodomo.persistence.R
import com.example.kodomo.persistence.databinding.ListFragmentBinding
import com.example.kodomo.persistence.db.entity.ProductEntity
import com.example.kodomo.persistence.model.Product
import com.example.kodomo.persistence.viewmodel.ProductListViewModel

/**
 * @author linxiaotao
 * 2018/12/24 下午4:23
 */
class ProductListFragment() : Fragment() {

    private lateinit var binding: ListFragmentBinding

    private val mProductClickCallback = object : ProductClickCallback {
        override fun onClick(product: Product) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                (activity as MainActivity).show(product)
            }
        }
    }

    private val productAdapter = ProductAdapter(mProductClickCallback)

    companion object {
        const val TAG = "ProductListFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.list_fragment, container, false)
        binding.productsList.layoutManager = LinearLayoutManager(context)
        binding.productsList.adapter = productAdapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewMode = ViewModelProviders.of(this)[ProductListViewModel::class.java]
        binding.productsSearchBtn.setOnClickListener {
            val query = binding.productsSearchBox.text
            if (query != null && !query.isEmpty()) {
                subscribeUI(viewMode.searchProducts("*${query}*"))
            } else {
                subscribeUI(viewMode.products())
            }
        }

        subscribeUI(viewMode.products())

    }

    fun subscribeUI(liveData: LiveData<List<ProductEntity>>) {
        liveData.observe(this, Observer { products ->
            products?.let {
                binding.isLoading = false
                productAdapter.setProductList(products)
            } ?: let {
                binding.isLoading = true
            }
            binding.executePendingBindings()
        })
    }

}