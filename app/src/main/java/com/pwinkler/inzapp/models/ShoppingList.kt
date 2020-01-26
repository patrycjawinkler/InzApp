package com.pwinkler.inzapp.models

data class ShoppingList (
    val id: String,
    val name: String,
    val items: ArrayList<String>,
    val users: List<String>?
)