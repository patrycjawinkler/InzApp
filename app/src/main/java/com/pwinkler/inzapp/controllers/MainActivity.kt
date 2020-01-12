package com.pwinkler.inzapp.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.adapters.RecipesListRecycleAdapter
import kotlin.random.Random
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var navigationView : NavigationView

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.my_recipes_card -> goToRecipesListActivity()
            R.id.shopping_list_card -> goToShoppingListActivity()
            R.id.random_recipe_card -> goToRandomRecipeActivity()
            R.id.propose_a_recipe_card -> goToProposeRecipeActivity()
        }
    }

    /**
     * Jeżeli użytkownik się wylogował to pokazujemy ekran logowania
     * jeżeli użytkownik jest zalogowany to pokazujemy mu ekran wyboru
     */

    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
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
        finishAffinity()
        exitProcess(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setCheckedItem(R.id.nav_main)
        navigationView.apply{
            setHeader(fbAuth.currentUser?.email)
            setHeaderUsername(fbAuth.currentUser?.displayName)
            setLogoutAction {
                fbAuth.signOut()
            }
            setMainActivityAction {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(intent)
            }
            setRecipeListAction {
                val intent = Intent(this@MainActivity, RecipesListActivity::class.java)
                startActivity(intent)
            }
            setShoppingListAction {
                val intent = Intent(this@MainActivity, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            setRandomRecipeAction {
                val intent = Intent(this@MainActivity, RandomRecipeActivity::class.java)
                startActivity(intent)
            }
            setGiveRecipeAction {
                val intent = Intent(this@MainActivity, ProposeRecipeActivity::class.java)
                startActivity(intent)
            }
            setFavoriteRecipesAction {
                val intent = Intent(this@MainActivity, FavoriteListActivity::class.java)
                startActivity(intent)
            }
            setChosenRecipesAction {
                val intent = Intent(this@MainActivity, ChosenListActivity::class.java)
                startActivity(intent)
            }
        }

        val recipesListCV = findViewById<CardView>(R.id.my_recipes_card)
        val shoppingListCV = findViewById<CardView>(R.id.shopping_list_card)
        val randomRecipeCV = findViewById<CardView>(R.id.random_recipe_card)
        val proposeRecipeCV = findViewById<CardView>(R.id.propose_a_recipe_card)

        recipesListCV.setOnClickListener(clickListener)
        shoppingListCV.setOnClickListener(clickListener)
        randomRecipeCV.setOnClickListener(clickListener)
        proposeRecipeCV.setOnClickListener(clickListener)
    }

    private fun goToRecipesListActivity() {
        val intent = Intent(this, RecipesListActivity::class.java)
        startActivity(intent)

    }

    private fun goToShoppingListActivity() {
        val intent = Intent(this, ShoppingListActivity::class.java)
        startActivity(intent)
    }

    private fun goToRandomRecipeActivity() {
        val intent = Intent(this, RandomRecipeActivity::class.java)
        startActivity(intent)
    }

    private fun goToProposeRecipeActivity() {
        val intent = Intent(this, ProposeRecipeActivity::class.java)
        startActivity(intent)
    }
}
