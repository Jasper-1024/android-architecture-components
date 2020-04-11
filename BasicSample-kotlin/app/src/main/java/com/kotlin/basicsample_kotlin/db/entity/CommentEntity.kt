package com.kotlin.basicsample_kotlin.db.entity

import androidx.room.*
import com.kotlin.basicsample_kotlin.model.Comment
import java.util.*

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
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
    override fun Id(): Int = Id()
    @Ignore
    override fun ProductID(): Int = productId
    @Ignore
    override fun Text(): String = text
    @Ignore
    override fun PostedAt(): Date? = postedAt
}