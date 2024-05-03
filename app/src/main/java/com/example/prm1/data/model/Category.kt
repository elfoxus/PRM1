package com.example.prm1.data.model

enum class Category {
    MEDICINES(0),
    COSMETICS(1),
    FOOD(2);

    private val id: Int

    constructor(id: Int) {
        this.id = id
    }

    fun getId(): Int {
        return id
    }

    companion object {
        fun fromId(id: Int): Category {
            return when (id) {
                0 -> MEDICINES
                1 -> COSMETICS
                2 -> FOOD
                else -> throw IllegalArgumentException("Invalid category id")
            }
        }
    }
}