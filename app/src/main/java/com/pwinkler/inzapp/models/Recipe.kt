package com.pwinkler.inzapp.models

data class Recipe(
    val rid: String,
    val name: String,
    val description: String,
    val image: String,
    val time: String,
    val meal_type: String,
    val dish_type: String,
    val ingredients: ArrayList<String>
)