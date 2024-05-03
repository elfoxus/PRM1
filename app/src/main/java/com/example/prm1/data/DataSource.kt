package com.example.prm1.data

import com.example.prm1.R
import com.example.prm1.data.model.Category
import com.example.prm1.data.model.Product
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

object DataSource {
    val products = mutableListOf<Product>(
        Product(
            resId = R.drawable.img,
            name = "Paracetamol",
            expirationDate = LocalDateTime.now().plusYears(1).toInstant(ZoneOffset.UTC),
            category = Category.MEDICINES,
            quantity = 10,
            thrownAway = false
        ),
        Product(
            resId = R.drawable.img,
            name = "Bread",
            expirationDate = LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.UTC),
            category = Category.FOOD,
            quantity = 1,
            thrownAway = false
        ),
        Product(
            resId = R.drawable.img,
            name = "Shampoo",
            expirationDate = LocalDateTime.now().plusYears(3).toInstant(ZoneOffset.UTC),
            category = Category.COSMETICS,
            quantity = 1,
            thrownAway = false
        ),
        Product(
            resId = R.drawable.img,
            name = "Toothpaste",
            expirationDate = LocalDateTime.now().plusYears(2).toInstant(ZoneOffset.UTC),
            category = Category.COSMETICS,
            quantity = 1,
            thrownAway = false
        ),
        Product(
            resId = R.drawable.img,
            name = "Milk",
            expirationDate = LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
            category = Category.FOOD,
            quantity = 1,
            thrownAway = false
        ),

    )
}