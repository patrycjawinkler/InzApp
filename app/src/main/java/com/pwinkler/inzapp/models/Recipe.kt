package com.pwinkler.inzapp.models

data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val image_id: String,
    val time_to_prepare: String,
    val meal_type: String,
    val dish_type: String,
    val ingredients: ArrayList<String>,
    val users: List<String>?
)