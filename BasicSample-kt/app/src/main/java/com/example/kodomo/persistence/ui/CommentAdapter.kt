package com.example.kodomo.persistence.ui

import android.database.DatabaseUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kodomo.persistence.R
import com.example.kodomo.persistence.databinding.CommentItemBinding
import com.example.kodomo.persistence.model.Comment
import java.util.*

/**
 * @author linxiaotao
 * 2018/12/28 下午8:57
 */
class CommentAdapter(private val commentClickCallback: CommentClickCallback) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val commentList = mutableListOf<Comment>()

    fun setCommentList(comments: List<Comment>) {
        if (commentList.isEmpty()) {
            commentList.addAll(comments)
            notifyItemRangeInserted(0, commentList.size)
        } else {
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

                override fun getNewListSize(): Int {
                    return comments.size
                }

                override fun getOldListSize(): Int {
                    return commentList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return commentList[oldItemPosition].id() == comments[newItemPosition].id()
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return areItemsTheSame(oldItemPosition, newItemPosition)
                            && commentList[oldItemPosition].postedAt() == comments[newItemPosition].postedAt()
                            && commentList[oldItemPosition].productId() == comments[newItemPosition].productId()
                            && Objects.equals(commentList[oldItemPosition].text(), comments[newItemPosition].text())
                }

            })

            commentList.addAll(comments)
            diffResult.dispatchUpdatesTo(this)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = DataBindingUtil.inflate<CommentItemBinding>(
                LayoutInflater.from(parent.context), R.layout.comment_item, parent, false
        )
        binding.callback = commentClickCallback
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.binding.comment = commentList[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    class CommentViewHolder(val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}