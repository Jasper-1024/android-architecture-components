package com.kotlin.basicsample_kotlin.db

import com.kotlin.basicsample_kotlin.db.entity.CommentEntity
import com.kotlin.basicsample_kotlin.db.entity.ProductEntity
import java.util.*
import java.util.concurrent.TimeUnit

object DataGenerator{
    val FIRST: Array<String> = arrayOf(
        "Special edition", "New", "Cheap", "Quality", "Used"
    )
    val SECOND: Array<String> = arrayOf(
        "Three-headed Monkey", "Rubber Chicken", "Pint of Grog", "Monocle"
    )
    val DESCRIPTION: Array<String> = arrayOf(
        "is finally here", "is recommended by Stan S. Stanman",
        "is the best sold product on Mêlée Island", "is \uD83D\uDCAF", "is ❤️", "is fine"
    )
    val COMMENTS: Array<String> = arrayOf(
        "Comment 1", "Comment 2", "Comment 3", "Comment 4", "Comment 5", "Comment 6"
    )

    fun generateProducts(): List<ProductEntity> {
        val products = mutableListOf<ProductEntity>()
        val rnd = Random()
        for (first in FIRST.indices) {
            for (second in SECOND.indices) {
                val product = ProductEntity()
                product.name = FIRST[first] + " " + SECOND[second]
                product.description = product.name + " " + DESCRIPTION[second]
                product.price = rnd.nextInt(240)
                product.id = FIRST.size * first + second + 1
                products.add(product)
            }
        }
        return products
    }

    fun generateCommentsForProducts(products: List<ProductEntity>): List<CommentEntity> {
        val comments = mutableListOf<CommentEntity>()
        val rnd = Random()
        for (product in products) {
            val commentsNumber = rnd.nextInt(5) + 1
            for (i in 0..commentsNumber) {
                val comment = CommentEntity()
                comment.productId = product.id
                comment.text = COMMENTS[i] + " for " + product.name
                comment.postedAt = Date(
                    System.currentTimeMillis()
                            - TimeUnit.DAYS.toMillis((commentsNumber - i).toLong())
                            + TimeUnit.HOURS.toMillis(i.toLong())
                )
                comments.add(comment)
            }
        }
        return comments
    }

}