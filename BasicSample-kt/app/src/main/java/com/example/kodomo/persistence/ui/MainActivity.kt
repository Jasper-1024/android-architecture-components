package com.example.kodomo.persistence.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kodomo.persistence.R
import com.example.kodomo.persistence.model.Product

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState ?: let {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, ProductListFragment(), ProductListFragment.TAG)
                    .commit()
        }

    }


    fun show(product: Product) {

        supportFragmentManager.beginTransaction()
                .addToBackStack("product")
                .replace(R.id.fragment_container, ProductFragment.forProduct(product.id()), null)
                .commit()

    }

}
