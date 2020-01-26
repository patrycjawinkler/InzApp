package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.helpers.DpSize
import com.pwinkler.inzapp.viewmodels.RecipeViewModel

class RandomRecipeActivity: AppCompatActivity() {

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var recipeViewModel: RecipeViewModel
    private lateinit var navigationView: NavigationView
    private lateinit var navigationDrawer: DrawerLayout

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser == null) {
            val intent = Intent(this@RandomRecipeActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        fbAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        fbAuth.removeAuthStateListener(authStateListener)
    }

    override fun onBackPressed() {
        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_recipe)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)

        navigationDrawer = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.apply {
            setHeader(fbAuth.currentUser?.email)
            setHeaderUsername(fbAuth.currentUser?.displayName)
            setLogoutAction {
                fbAuth.signOut()
            }
            setRecipeListAction {
                val intent = Intent(this@RandomRecipeActivity, RecipesListActivity::class.java)
                startActivity(intent)
            }
            setShoppingListAction {
                val intent = Intent(this@RandomRecipeActivity, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            setRandomRecipeAction {
                val intent = Intent(this@RandomRecipeActivity, RandomRecipeActivity::class.java)
                startActivity(intent)
            }
            setGiveRecipeAction {
                val intent = Intent(this@RandomRecipeActivity, ProposeRecipeActivity::class.java)
                startActivity(intent)
            }
            setFavoriteRecipesAction {
                val intent = Intent(this@RandomRecipeActivity, FavoriteListActivity::class.java)
                startActivity(intent)
            }
            setChosenRecipesAction {
                val intent = Intent(this@RandomRecipeActivity, ChosenListActivity::class.java)
                startActivity(intent)
            }
        }



    }
}