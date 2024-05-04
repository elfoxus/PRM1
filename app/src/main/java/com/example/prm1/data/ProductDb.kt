package com.example.prm1.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.prm1.data.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 2
)
abstract class ProductDb : RoomDatabase() {
    abstract val productDao: ProductDao

    companion object {
        fun open(context: Context): ProductDb = Room
            .databaseBuilder(context, ProductDb::class.java, "product.db")
            .addMigrations(ProductDbMigration1To2())
            .build()
    }
}

/**
 * Migracja z wersji 1 do 2
 * Dodaje kolumnę image do tabeli product, początkowo nie była ona dodana.
 * Wartość domyślna to 'mleko'
 * @see ProductEntity
 */
class ProductDbMigration1To2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE product ADD COLUMN image TEXT NOT NULL DEFAULT 'mleko'")
    }
}