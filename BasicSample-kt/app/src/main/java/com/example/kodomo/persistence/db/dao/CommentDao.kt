package com.example.kodomo.persistence.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kodomo.persistence.db.entity.CommentEntity

/**
 * @author linxiaotao
 * 2018/12/27 下午8:20
 */
@Dao
interface CommentDao {

    @Query("select * from comments where productId = :productId")
    fun loadComments(productId: Int): LiveData<List<CommentEntity>>

    @Query("select * from comments where productId = :productId")
    fun loadCommentsSync(productId: Int): List<CommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(comments: List<CommentEntity>)
}