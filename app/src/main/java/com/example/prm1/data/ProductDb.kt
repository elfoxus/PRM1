package com.example.prm1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.prm1.data.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1
)
abstract class ProductDb : RoomDatabase() {
    abstract val productDao: ProductDao

    companion object {
        fun open(context: Context): ProductDb = Room
            .databaseBuilder(context, ProductDb::class.java, "product.db")
            .build()
    }
}