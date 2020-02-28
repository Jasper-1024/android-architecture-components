package com.example.kodomo.persistence.db.entity

import androidx.room.*
import com.example.kodomo.persistence.model.Comment
import java.util.*

/**
 * @author linxiaotao
 * 2018/12/27 下午5:47
 */
@Entity(tableName = "comments",
        foreignKeys = [
            ForeignKey(entity = ProductEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["productId"],
                    onDelete = ForeignKey.CASCADE)
        ],
        indices = [Index(value = ["productId"])]
)
data class CommentEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var productId: Int = 0,
        var text: String = "",
        var postedAt: Date? = null
) : Comment {


    @Ignore
    override fun id() = id

    @Ignore
    override fun productId() = productId

    @Ignore
    override fun text() = text

    @Ignore
    override fun postedAt() = postedAt

}