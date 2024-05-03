package com.example.prm1.data.model

import androidx.annotation.DrawableRes
import java.io.Serializable
import java.time.Instant

data class Product(
    @DrawableRes
    var resId: Int,
    var name: String,
    var expirationDate: Instant,
    var category: Category,
    var quantity: Int = 1,
    var disposed: Boolean = false,
) : Serializable
