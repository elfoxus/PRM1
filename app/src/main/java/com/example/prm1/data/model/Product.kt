package com.example.prm1.data.model

import android.content.res.Resources
import androidx.annotation.DrawableRes
import com.example.prm1.R
import com.example.prm1.Utils
import com.example.prm1.data.entity.ProductEntity
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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
        return Utils.millisToCalendar(expirationDate.toEpochMilli()).isBeforeByDays(Calendar.getInstance())
    }

    fun isLastDay(): Boolean {
        val calendar = Utils.millisToCalendar(expirationDate.toEpochMilli())
        return calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }

    fun Calendar.isBeforeByDays(other: Calendar): Boolean {
        return this.get(Calendar.YEAR) < other.get(Calendar.YEAR) ||
                (this.get(Calendar.YEAR) == other.get(Calendar.YEAR) && this.get(Calendar.DAY_OF_YEAR) < other.get(
                    Calendar.DAY_OF_YEAR))
    }

    fun toEntity(resources: Resources): ProductEntity {
        return ProductEntity(
            id = id,
            name = name,
            expirationDate = expirationDate.toEpochMilli(),
            category = category.getId(),
            quantity = quantity,
            disposed = disposed,
            image = resources.getResourceEntryName(resId)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}
