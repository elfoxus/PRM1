package com.example.prm1.data

import androidx.room.*
import com.example.prm1.Utils
import com.example.prm1.data.entity.ProductEntity
import java.util.Calendar

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun getAll(): List<ProductEntity>

    @Insert
    fun insertAll(products: Array<ProductEntity>)

    @androidx.room.Query("SELECT * FROM product ORDER BY expirationDate ASC")
    fun getAllSortedByExpirationDate(): List<ProductEntity>

    @androidx.room.Query("SELECT * FROM product WHERE category = :categoryId ORDER BY expirationDate ASC")
    fun getAllSortedByExpirationDateForCategory(categoryId: Int): List<ProductEntity>

    @androidx.room.Query("SELECT * FROM product WHERE expirationDate < :someDate ORDER BY expirationDate ASC")
    fun getAllSortedByExpirationWithDateBefore(someDate: Long): List<ProductEntity>

    fun getExpiredWithCategorySortedByExpirationDate(categoryId: Int): List<ProductEntity> {
        return getAllSortedByExpirationDateForCategory(categoryId)
            .filter { millisToCalendar(it.expirationDate)
                .isBeforeByDays(Calendar.getInstance())}
    }

    fun getExpiredSortedByExpirationDate(): List<ProductEntity> {
        return getAllSortedByExpirationDate()
            .filter { millisToCalendar(it.expirationDate)
                .isBeforeByDays(Calendar.getInstance())}
    }

    fun getNotExpiredSortedByExpirationDate(): List<ProductEntity> {
        return getAllSortedByExpirationDate()
            .filter { !millisToCalendar(it.expirationDate)
                .isBeforeByDays(Calendar.getInstance())}
    }

    fun getNotExpiredSortedByExpirationDateWithCategory(categoryId: Int): List<ProductEntity> {
        return getAllSortedByExpirationDateForCategory(categoryId)
            .filter { !millisToCalendar(it.expirationDate)
                .isBeforeByDays(Calendar.getInstance())}
    }

    private fun millisToCalendar(millis: Long): Calendar {
        return Utils.millisToCalendar(millis)
    }

    fun Calendar.isBeforeByDays(other: Calendar): Boolean {
        return this.get(Calendar.YEAR) < other.get(Calendar.YEAR) ||
                (this.get(Calendar.YEAR) == other.get(Calendar.YEAR) && this.get(Calendar.DAY_OF_YEAR) < other.get(Calendar.DAY_OF_YEAR))
    }


    @androidx.room.Query("SELECT * FROM product WHERE id = :id")
    fun getById(id: Long): ProductEntity

    @Insert
    fun addProduct(product: ProductEntity): Long
    @Delete
    fun removeProduct(product: ProductEntity)
    @Update
    fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM product")
    fun removeAll()
}