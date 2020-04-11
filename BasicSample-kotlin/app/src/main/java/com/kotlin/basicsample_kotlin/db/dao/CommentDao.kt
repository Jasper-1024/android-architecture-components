package com.kotlin.basicsample_kotlin.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kotlin.basicsample_kotlin.db.entity.CommentEntity

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments where productId = :productId")
    fun loadComments(productId:Int): LiveData<List<CommentEntity>>
    @Query("SELECT * FROM comments where productId = :productId")
    fun loadCommentsSync(productId: Int):List<CommentEntity>
    //插入数据 主键相同,旧数据会替换新数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(comments: List<CommentEntity>)
}