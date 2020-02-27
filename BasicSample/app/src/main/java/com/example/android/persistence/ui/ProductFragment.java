/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.persistence.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.persistence.R;
import com.example.android.persistence.databinding.ProductFragmentBinding;
import com.example.android.persistence.viewmodel.ProductViewModel;
//评论的片段
public class ProductFragment extends Fragment {

    private static final String KEY_PRODUCT_ID = "product_id";
    //绑定类
    private ProductFragmentBinding mBinding;
    //list 适配器
    private CommentAdapter mCommentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        // Inflate this data binding layout //生成绑定类
        mBinding = DataBindingUtil.inflate(inflater, R.layout.product_fragment, container, false);

        // Create and set the adapter for the RecyclerView.//设置list 适配器
        mCommentAdapter = new CommentAdapter(mCommentClickCallback);
        mBinding.commentList.setAdapter(mCommentAdapter);
        return mBinding.getRoot();
    }

    private final CommentClickCallback mCommentClickCallback = comment -> {
        // no-op
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //工厂类创建 ViewModel
        ProductViewModel.Factory factory = new ProductViewModel.Factory(
                requireActivity().getApplication(), requireArguments().getInt(KEY_PRODUCT_ID));

        final ProductViewModel model = new ViewModelProvider(this, factory)
                .get(ProductViewModel.class);
        //databinding 与 livedata 配合
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        //绑定数据
        mBinding.setProductViewModel(model);

        subscribeToModel(model);
    }

    private void subscribeToModel(final ProductViewModel model) {
        // Observe comments livedata 绑定生命周期
        model.getComments().observe(getViewLifecycleOwner(), commentEntities -> {
            if (commentEntities != null) {
                mBinding.setIsLoading(false);
                mCommentAdapter.setCommentList(commentEntities);
            } else {
                mBinding.setIsLoading(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        //防止内存泄漏
        mBinding = null;
        mCommentAdapter = null;
        super.onDestroyView();
    }
    //创建 评论的片段
    /** Creates product fragment for specific product ID */
    public static ProductFragment forProduct(int productId) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }
}
