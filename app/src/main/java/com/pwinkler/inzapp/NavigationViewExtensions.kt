package com.pwinkler.inzapp

import com.google.android.material.navigation.NavigationView
import com.pwinkler.inzapp.models.User
import kotlinx.android.synthetic.main.navigation_header.view.*

/**
 * Ustawia adres e-mail zalogowanego użytkownika jako jego nazwę
 * ZMIENIC ADRES E-MAIL NA USERNAME
 */

fun NavigationView.setHeader(email: String? = "") {
    val headerView = this.getHeaderView(0)
    headerView.email_address_text.text = email
}

fun NavigationView.setHeaderUsername(username: String? = "") {
    val headerView = this.getHeaderView(0)
    headerView.username_text.text = username
}

/**
 * Podpinanie przycisków, którym wykonują akcje
 * Przechodzenie do kolejnych ekranów lub wylogowanie się
 */

fun NavigationView.setLogoutAction(onClick: () -> Unit) {
    val navigationLogoutButton = this.menu.findItem(R.id.nav_logout)
    navigationLogoutButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}

fun NavigationView.setMainActivityAction(onClick: () -> Unit) {
    val navigationMainActivityButton = this.menu.findItem(R.id.nav_main)
    navigationMainActivityButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}

fun NavigationView.setRecipeListAction(onClick: () -> Unit) {
    val navigationRecipeListButton = this.menu.findItem(R.id.nav_my_recipes)
    navigationRecipeListButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}

fun NavigationView.setShoppingListAction(onClick: () -> Unit) {
    val navigationShoppingListButton = this.menu.findItem(R.id.nav_shopping_list)
    navigationShoppingListButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}

fun NavigationView.setRandomRecipeAction(onClick: () -> Unit) {
    val randomRecipeButton = this.menu.findItem(R.id.nav_random_recipe)
    randomRecipeButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}

fun NavigationView.setGiveRecipeAction(onClick: () -> Unit) {
    val giveRecipeButton = this.menu.findItem(R.id.nav_filter_recipes)
    giveRecipeButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}

fun NavigationView.setFavoriteRecipesAction(onClick: () -> Unit) {
    val favoriteRecipesButton = this.menu.findItem(R.id.nav_favorite_recipes)
    favoriteRecipesButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}

fun NavigationView.setChosenRecipesAction(onClick: () -> Unit) {
    val chosenRecipesButton = this.menu.findItem(R.id.nav_chosen_recipes)
    chosenRecipesButton.setOnMenuItemClickListener {
        onClick()
        true
    }
}