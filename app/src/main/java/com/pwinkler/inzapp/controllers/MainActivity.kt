package com.pwinkler.inzapp.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.adapters.RecipesListRecycleAdapter
import com.pwinkler.inzapp.R
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var recipesListRecycleAdapter: RecipesListRecycleAdapter

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
        //setSupportActionBar(findViewById(R.id.toolbar))

        val logoutButton = findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            fbAuth.signOut()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val recipesListCV = findViewById<CardView>(R.id.my_recipes_card)
        recipesListCV.setOnClickListener {
            startActivity(Intent(this, RecipesListActivity::class.java))
        }

        val shoppingListCV = findViewById<CardView>(R.id.shopping_list_card)
        shoppingListCV.setOnClickListener {
            startActivity(Intent(this, ShoppingListActivity::class.java))
        }

        val randomRecipeCV = findViewById<CardView>(R.id.random_recipe_card)
        randomRecipeCV.setOnClickListener {
            startActivity(Intent(this, RandomRecipeActivity::class.java))
        }

        val proposeRecipeCV = findViewById<CardView>(R.id.propose_a_recipe_card)
        proposeRecipeCV.setOnClickListener {
            startActivity(Intent(this, ProposeRecipeActivity::class.java))
        }
    }
}
