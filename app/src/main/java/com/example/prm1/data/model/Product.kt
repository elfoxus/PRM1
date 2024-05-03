package com.example.prm1.data.model

import androidx.annotation.DrawableRes
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate

data class Product(
    @DrawableRes
    var resId: Int,
    var name: String,
    var expirationDate: Instant,
    var category: Category,
    var quantity: Int = 1,
    var thrownAway: Boolean = false,
) : Serializable
