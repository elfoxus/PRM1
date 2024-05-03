package com.example.prm1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val expirationDate: Long,
    val category: Int,
    val quantity: Int,
    val disposed: Boolean,
)