package com.example.prm1.data.model

import androidx.annotation.DrawableRes
import com.example.prm1.R
import com.example.prm1.data.entity.ProductEntity
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        fun fromEntity(entity: ProductEntity, resId: Int): Product {
            return Product(
                id = entity.id,
                resId = resId,
                name = entity.name,
                expirationDate = Instant.ofEpochMilli(entity.expirationDate),
                category = Category.fromId(entity.category),
                quantity = entity.quantity,
                disposed = entity.disposed,
            )
        }
    }

    fun getDateString(): String {
        return LocalDateTime.ofInstant(expirationDate, ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    fun isExpired(): Boolean {
        return expirationDate.isBefore(Instant.now())
    }
}
