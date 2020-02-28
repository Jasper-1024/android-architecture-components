package com.example.kodomo.persistence.ui

import com.example.kodomo.persistence.model.Comment

/**
 * @author linxiaotao
 * 2018/12/28 下午8:57
 */
interface CommentClickCallback {

    fun onClick(comment: Comment)

}