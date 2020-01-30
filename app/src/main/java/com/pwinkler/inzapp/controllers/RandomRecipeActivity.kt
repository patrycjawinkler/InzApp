package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.helpers.DpSize
import com.pwinkler.inzapp.viewmodels.RecipeViewModel
import com.pwinkler.inzapp.viewmodels.ShoppingListViewModel
import com.squareup.picasso.Picasso

class RandomRecipeActivity: AppCompatActivity() {

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var recipeViewModel: RecipeViewModel
    private lateinit var navigationView: NavigationView
    private lateinit var navigationDrawer: DrawerLayout

    private val db = FirebaseFirestore.getInstance()
    private val collectionPath = "/recipes"

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

        setSupportActionBar(findViewById(R.id.random_recipe_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.title = "Losuj przepis"

        navigationDrawer = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setCheckedItem(R.id.nav_random_recipe)
        navigationView.apply {
            setHeader(fbAuth.currentUser?.email)
            setHeaderUsername(fbAuth.currentUser?.displayName)
            setLogoutAction {
                fbAuth.signOut()
            }
            setMainActivityAction {
                val intent = Intent(this@RandomRecipeActivity, MainActivity::class.java)
                startActivity(intent)
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

        recipeViewModel = ViewModelProviders.of(this@RandomRecipeActivity).get(RecipeViewModel::class.java)
        recipeViewModel.getAllUserRecipes()

        val drawRecipeButton = findViewById<Button>(R.id.draw_recipe_button)
        drawRecipeButton.setOnClickListener {

            val allRecipesArray = arrayListOf<String>()

            val recipeListItemContainer = findViewById<ConstraintLayout>(R.id.recipe_list_item_container)
            val dishImageView = findViewById<ImageView>(R.id.dish_image)
            val dishNameTextView = findViewById<TextView>(R.id.dish_name)
            val timeToPrepareIcon = findViewById<ImageView>(R.id.time_ic)
            val timeToPrepareTextView = findViewById<TextView>(R.id.time_to_prepare)
            val dishTypeIcon = findViewById<ImageView>(R.id.dish_type_ic)
            val dishTypeTextView = findViewById<TextView>(R.id.dish_type_text)
            val mealTypeIcon = findViewById<ImageView>(R.id.meal_type_ic)
            val mealTypeTextView = findViewById<TextView>(R.id.meal_type_text)
            val favoriteIcon = findViewById<ImageView>(R.id.favorite_ic)
            val chosenIcon = findViewById<ImageView>(R.id.chosen_ic)

            db.collection(collectionPath).whereArrayContains("users", fbAuth.currentUser?.uid ?: "")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        allRecipesArray.add(document.id)
                    }
                    val randomRecipeId = allRecipesArray.random()
                    Log.d("TAG", "Losowy przepis: ${allRecipesArray.random()}")

                    val randomRecipe = db.collection(collectionPath).document(randomRecipeId)
                    randomRecipe.get()
                        .addOnSuccessListener { document ->
                            if (document.getString("image_id")!!.isNotBlank()){
                                Picasso.get().load(document.getString("image_id")).into(dishImageView)
                            }
                            dishNameTextView?.text = document.getString("name")
                            timeToPrepareIcon?.setImageResource(R.drawable.ic_time_24dp)
                            timeToPrepareTextView?.text = document.getString("time_to_prepare")
                            dishTypeIcon?.setImageResource(R.drawable.ic_dish_24dp)
                            dishTypeTextView?.text = document.getString("dish_type")
                            mealTypeIcon?.setImageResource(R.drawable.ic_meal_type_24dp)
                            mealTypeTextView?.text = document.getString("meal_type")
                            if (document.getBoolean("favorite")!!) {
                                favoriteIcon?.setImageResource(R.drawable.ic_favorite_24dp)
                            } else {
                                favoriteIcon?.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                            }
                            if (document.getBoolean("chosen")!!) {
                                chosenIcon?.setImageResource(R.drawable.ic_bookmark_black_24dp)
                            } else {
                                chosenIcon?.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
                            }
                        }
                    recipeListItemContainer.setOnClickListener {
                        goToRecipe(randomRecipeId)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "Error getting documents: ", exception)
                }
        }
    }

    /**
     * Funkcja, która odsyła do wybranego przepisu
     */
    private fun goToRecipe(recipeId: String) {
        val intent = Intent(this, RecipeActivity::class.java)
            .apply {
                putExtra(RecipesListActivity.EXTRA_RECIPE_ID, recipeId)
            }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigationDrawer.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

}