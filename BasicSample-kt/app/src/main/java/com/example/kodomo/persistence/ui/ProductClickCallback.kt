package com.example.kodomo.persistence.ui

import com.example.kodomo.persistence.model.Product

/**
 * @author linxiaotao
 * 2018/12/24 下午5:46
 */
interface ProductClickCallback {

    fun onClick(product: Product)
}