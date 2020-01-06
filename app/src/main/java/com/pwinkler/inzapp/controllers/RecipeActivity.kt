package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.*
import com.squareup.picasso.Picasso
import com.pwinkler.inzapp.controllers.RecipesListActivity.Companion.EXTRA_RECIPE_ID
import com.pwinkler.inzapp.helpers.DpSize
import java.util.*

class RecipeActivity : AppCompatActivity() {

    private val recipeCollectionPath = "/recipes"
    private val productCollectionPath = "/products"

    private lateinit var navigationDrawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var dpSize: DpSize


    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //Jeśli użytkownik się wylogował, pokazujemy ekran logowania
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser == null) {
            val intent = Intent(this@RecipeActivity, LoginActivity::class.java)
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
        setContentView(R.layout.activity_recipe)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)

        dpSize = DpSize(this@RecipeActivity)

        navigationDrawer = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.apply {
            setHeader(fbAuth.currentUser?.email)
            setLogoutAction {
                fbAuth.signOut()
            }
            setRecipeListAction {
                val intent = Intent(this@RecipeActivity, RecipesListActivity::class.java)
                startActivity(intent)
            }
            setShoppingListAction {
                val intent = Intent(this@RecipeActivity, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            setRandomRecipeAction {
                val intent = Intent(this@RecipeActivity, RandomRecipeActivity::class.java)
                startActivity(intent)
            }
            setGiveRecipeAction {
                val intent = Intent(this@RecipeActivity, ProposeRecipeActivity::class.java)
                startActivity(intent)
            }
            setFavoriteRecipesAction {
                val intent = Intent(this@RecipeActivity, FavoriteListActivity::class.java)
                startActivity(intent)
            }
            setChosenRecipesAction {
                val intent = Intent(this@RecipeActivity, ChosenListActivity::class.java)
                startActivity(intent)
            }
        }

        val dishImageView = findViewById<ImageView>(R.id.dish_image)
        val favoriteImageView = findViewById<ImageView>(R.id.favorite_ic)
        val chosenImageView = findViewById<ImageView>(R.id.chosen_ic)
        val dishNameTextView = findViewById<TextView>(R.id.dish_name)
        val timeToPrepareIcon = findViewById<ImageView>(R.id.time_ic)
        val timeToPrepareTextView = findViewById<TextView>(R.id.time_to_prepare)
        val dishTypeIcon = findViewById<ImageView>(R.id.dish_type_ic)
        val dishTypeTextView = findViewById<TextView>(R.id.dish_type_text)
        val mealTypeIcon = findViewById<ImageView>(R.id.meal_type_ic)
        val mealTypeTextView = findViewById<TextView>(R.id.meal_type_text)
        val recipeDescriptionTextView = findViewById<TextView>(R.id.recipe_description)
        val container = findViewById<LinearLayout>(R.id.ingredients_container)

        val activity = this@RecipeActivity

        val selectedRecipeId = intent.getStringExtra(EXTRA_RECIPE_ID)
        val recipeReference = db.collection(recipeCollectionPath).document(selectedRecipeId!!)

        recipeReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.getString("image_id")!!.isNotBlank()) {
                        Picasso.get().load(document.getString("image_id")).into(dishImageView)
                    }
                    favoriteImageView?.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    chosenImageView?.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
                    dishNameTextView?.text = document.getString("name")
                    timeToPrepareIcon?.setImageResource(R.drawable.ic_time_24dp)
                    timeToPrepareTextView?.text = document.getString("time_to_prepare")
                    dishTypeIcon?.setImageResource(R.drawable.ic_dish_24dp)
                    dishTypeTextView?.text = document.getString("dish_type")
                    mealTypeIcon?.setImageResource(R.drawable.ic_meal_type_24dp)
                    mealTypeTextView?.text = document.getString("meal_type")
                    recipeDescriptionTextView?.text = document.getString("description")

                    val ingredient = document["ingredients"] as ArrayList<String>

                    for (i in 0 until ingredient.size) {

                        val productReference = db.collection(productCollectionPath).document(ingredient[i])

                        productReference.get()
                            .addOnSuccessListener { document2 ->
                                if (document2 != null) {

                                    val ingredientsContainer = LinearLayout(activity).apply {
                                        orientation = LinearLayout.VERTICAL
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    }

                                    val ingredientNameTextView = TextView(activity).apply {
                                        text = document2.getString("name")
                                        textSize = 16F

                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    }

                                    val quantityAndUnitContainer = LinearLayout(activity).apply {
                                        orientation = LinearLayout.HORIZONTAL
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    }

                                    val ingredientQuantityTextView = TextView(activity).apply {
                                        text = document2.getString("quantity")
                                        textSize = 14F
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    }

                                    quantityAndUnitContainer.addView(ingredientQuantityTextView)

                                    val ingredientUnitTextView = TextView(activity).apply {
                                        text = document2.getString("unit")
                                        textSize = 14F
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT
                                        )
                                        (layoutParams as LinearLayout.LayoutParams).setMargins(8, 0 , 0, 0)
                                    }

                                    quantityAndUnitContainer.addView(ingredientUnitTextView)

                                    val separator = View(activity).apply {
                                        setBackgroundColor(Color.parseColor("#3A0A0A0A"))
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            dpSize.dpToPx(1)
                                        )
                                    }

                                    ingredientsContainer.addView(ingredientNameTextView)
                                    ingredientsContainer.addView(quantityAndUnitContainer)

                                    ingredientsContainer.addView(separator)

                                    container.addView(ingredientsContainer)
                                } else {
                                    Log.d("TAG", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("TAG", "Get failed with product database", exception)
                            }
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Get failed with recipe database", exception)
            }
        }

    /**
     * Dodawanie ikonek do bottom app bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigationDrawer.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

}
