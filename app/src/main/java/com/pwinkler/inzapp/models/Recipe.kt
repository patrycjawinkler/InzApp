package com.pwinkler.inzapp.models

data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val imageId: Int,
    val time_to_prepare_ic: Int,
    val time_to_prepare: String,
    val meal_type: String,
    val dish_type_ic: Int,
    val dish_type: String,
    val ingredients: ArrayList<String>
)