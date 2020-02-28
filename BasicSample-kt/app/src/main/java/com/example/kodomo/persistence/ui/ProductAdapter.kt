package com.example.kodomo.persistence.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kodomo.persistence.R
import com.example.kodomo.persistence.databinding.ProductItemBinding
import com.example.kodomo.persistence.model.Product
import java.util.*


/**
 * @author linxiaotao
 * 2018/12/24 下午5:33
 */
class ProductAdapter(val clickCallback: ProductClickCallback) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    init {
        setHasStableIds(true)
    }

    val mProductList = mutableListOf<Product>()

    fun setProductList(productList: List<Product>) {
        if (mProductList.isEmpty()) {
            mProductList.addAll(productList)
            notifyItemRangeInserted(0, productList.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mProductList.size
                }

                override fun getNewListSize(): Int {
                    return productList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mProductList[oldItemPosition].id() ==
                            productList[newItemPosition].id()
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return productList[newItemPosition].id() == mProductList[oldItemPosition].id()
                            && Objects.equals(productList[newItemPosition].description(), mProductList[oldItemPosition].description())
                            && Objects.equals(productList[newItemPosition].name(), mProductList[oldItemPosition].name())
                            && productList[newItemPosition].price() == mProductList[oldItemPosition].price()
                }
            })

            mProductList.clear()
            mProductList.addAll(productList)
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding: ProductItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.product_item,
                parent,
                false
        )
        binding.callback = clickCallback
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.binding.product = mProductList[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mProductList.size
    }

    override fun getItemId(position: Int): Long {
        return mProductList[position].id().toLong()
    }

    class ProductViewHolder(val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {


    }

}