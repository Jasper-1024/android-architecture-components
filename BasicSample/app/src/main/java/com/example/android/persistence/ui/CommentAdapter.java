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

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.persistence.databinding.CommentItemBinding;
import com.example.android.persistence.model.Comment;
import com.example.android.persistence.R;

import java.util.List;
//RecyclerView.Adapter
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    //评论list
    private List<? extends Comment> mCommentList;

    @Nullable  //点击事件
    private final CommentClickCallback mCommentClickCallback;

    public CommentAdapter(@Nullable CommentClickCallback commentClickCallback) {
        mCommentClickCallback = commentClickCallback;
    }
    //设置显示 list
    public void setCommentList(final List<? extends Comment> comments) {
        if (mCommentList == null) {
            mCommentList = comments;
            //局部刷新,从位置0开始插入.
            notifyItemRangeInserted(0, comments.size());
        } else {
            //比较数据集差异
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mCommentList.size();
                }

                @Override
                public int getNewListSize() {
                    return comments.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Comment old = mCommentList.get(oldItemPosition);
                    Comment comment = comments.get(newItemPosition);
                    return old.getId() == comment.getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Comment old = mCommentList.get(oldItemPosition);
                    Comment comment = comments.get(newItemPosition);
                    return old.getId() == comment.getId()
                            && old.getPostedAt() == comment.getPostedAt()
                            && old.getProductId() == comment.getProductId()
                            && TextUtils.equals(old.getText(), comment.getText());
                }
            });
            mCommentList = comments;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @Override//创建 ViewHolder
    @NonNull
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //生成绑定类
        CommentItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.comment_item,
                        parent, false);
        //设置点击回调
        binding.setCallback(mCommentClickCallback);
        return new CommentViewHolder(binding);
    }

    @Override//绑定数据
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.binding.setComment(mCommentList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mCommentList == null ? 0 : mCommentList.size();
    }
    //全局单例
    static class CommentViewHolder extends RecyclerView.ViewHolder {

        final CommentItemBinding binding;

        CommentViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
