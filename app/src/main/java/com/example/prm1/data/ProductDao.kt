package com.example.prm1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.prm1.data.entity.ProductEntity

@Dao
interface ProductDao {
    @androidx.room.Query("SELECT * FROM product")
    fun getAll(): List<ProductEntity>

    @androidx.room.Query("SELECT * FROM product ORDER BY expirationDate ASC")
    fun getAllSortedByExpirationDate(): List<ProductEntity>

    @androidx.room.Query("SELECT * FROM product WHERE id = :id")
    fun getById(id: Long): ProductEntity

    @Insert
    fun addProduct(product: ProductEntity): Long
    @Delete
    fun removeProduct(product: ProductEntity)
    @Update
    fun updateProduct(product: ProductEntity)
}