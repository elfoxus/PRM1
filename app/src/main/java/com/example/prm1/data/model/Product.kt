package com.example.prm1.data.model

import androidx.annotation.DrawableRes
import com.example.prm1.R
import com.example.prm1.data.entity.ProductEntity
import java.io.Serializable
import java.time.Instant

data class Product(
    val id: Long,
    @DrawableRes
    var resId: Int,
    var name: String,
    var expirationDate: Instant,
    var category: Category,
    var quantity: Int = 1,
    var disposed: Boolean = false,
) : Serializable {
    companion object {
        fun fromEntity(entity: ProductEntity): Product {
            return Product(
                id = entity.id,
                resId = R.drawable.img,
                name = entity.name,
                expirationDate = Instant.ofEpochMilli(entity.expirationDate),
                category = Category.fromId(entity.category),
                quantity = entity.quantity,
                disposed = entity.disposed,
            )
        }
    }
}
