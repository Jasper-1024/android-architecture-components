package com.example.kodomo.persistence.ui

import android.content.ComponentCallbacks
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kodomo.persistence.R
import com.example.kodomo.persistence.databinding.ProductFragmentBinding
import com.example.kodomo.persistence.db.entity.CommentEntity
import com.example.kodomo.persistence.db.entity.ProductEntity
import com.example.kodomo.persistence.model.Comment
import com.example.kodomo.persistence.viewmodel.ProductViewModel

/**
 * @author linxiaotao
 * 2018/12/24 下午4:37
 */
class ProductFragment() : Fragment() {


    companion object {

        const val KEY_PRODUCT_ID = "product_id"

        fun forProduct(productId: Int): ProductFragment {
            val fragment = ProductFragment()
            val args = Bundle()
            args.putInt(KEY_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var binding: ProductFragmentBinding

    private lateinit var adapter: CommentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.product_fragment, container, false)
        adapter = CommentAdapter(object : CommentClickCallback {
            override fun onClick(comment: Comment) {

            }
        })
        binding.commentList.layoutManager = LinearLayoutManager(context)
        binding.commentList.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = ProductViewModel.Factor(
                activity!!.application, arguments!!.getInt(KEY_PRODUCT_ID)
        )

        val model = ViewModelProviders.of(this, factory)
                .get(ProductViewModel::class.java)

        binding.productViewModel = model

        subscribeToMode(model)
    }

    private fun subscribeToMode(model: ProductViewModel) {
        model.getObservableProduct().observe(this, object : Observer<ProductEntity> {
            override fun onChanged(t: ProductEntity?) {
                model.setProduct(t!!)
            }
        })

        model.getObservableComments().observe(this, object : Observer<List<CommentEntity>> {
            override fun onChanged(commentEntities: List<CommentEntity>?) {
                if (commentEntities != null) {
                    binding.isLoading = false
                    adapter.setCommentList(commentEntities)
                } else {
                    binding.isLoading = true
                }
            }
        })
    }

}