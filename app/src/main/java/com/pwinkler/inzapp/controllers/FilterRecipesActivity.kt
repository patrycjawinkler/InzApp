package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.adapters.RecipesListRecycleAdapter
import com.pwinkler.inzapp.viewmodels.RecipeViewModel

class FilterRecipesActivity: AppCompatActivity() {

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var recipeViewModel: RecipeViewModel
    lateinit var recipeRecycleAdapter: RecipesListRecycleAdapter

    private lateinit var navigationView: NavigationView
    private lateinit var navigationDrawer: DrawerLayout

    private val db = FirebaseFirestore.getInstance()
    private val collectionPath = "/recipes"

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser == null) {
            val intent = Intent(this@FilterRecipesActivity, LoginActivity::class.java)
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
        setContentView(R.layout.activity_filter_recipes)

        setSupportActionBar(findViewById(R.id.filtered_recipe_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.title = "Filtruj przepisy"

        navigationDrawer = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setCheckedItem(R.id.nav_filter_recipes)
        navigationView.apply {
            setHeader(fbAuth.currentUser?.email)
            setHeaderUsername(fbAuth.currentUser?.displayName)
            setLogoutAction {
                fbAuth.signOut()
            }
            setMainActivityAction {
                val intent = Intent(this@FilterRecipesActivity, MainActivity::class.java)
                startActivity(intent)
            }
            setRecipeListAction {
                val intent = Intent(this@FilterRecipesActivity, RecipesListActivity::class.java)
                startActivity(intent)
            }
            setShoppingListAction {
                val intent = Intent(this@FilterRecipesActivity, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            setRandomRecipeAction {
                val intent = Intent(this@FilterRecipesActivity, RandomRecipeActivity::class.java)
                startActivity(intent)
            }
            setGiveRecipeAction {
                val intent = Intent(this@FilterRecipesActivity, FilterRecipesActivity::class.java)
                startActivity(intent)
            }
            setFavoriteRecipesAction {
                val intent = Intent(this@FilterRecipesActivity, FavoriteListActivity::class.java)
                startActivity(intent)
            }
            setChosenRecipesAction {
                val intent = Intent(this@FilterRecipesActivity, ChosenListActivity::class.java)
                startActivity(intent)
            }
        }

        /**
         * Podpięcie opcji wyboru pod spinnery
         * simple_spinner_item i simple_spinner_dropdown_item to layouty wbudowane
         */

        val mealTypeSpinner = findViewById<Spinner>(R.id.meal_type_spinner)
        val dishTypeSpinner = findViewById<Spinner>(R.id.dish_type_spinner)
        val timeToPrepareSpinner = findViewById<Spinner>(R.id.time_to_prepare_spinner)
        val onlyFavoriteSpinner = findViewById<Spinner>(R.id.only_favorite_spinner)


        val activity = this@FilterRecipesActivity

        ArrayAdapter.createFromResource(
            activity,
            R.array.meal_types_filtering,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mealTypeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            activity,
            R.array.dish_types_filtering,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dishTypeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            activity,
            R.array.time_to_prepare_filtering,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timeToPrepareSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            activity,
            R.array.only_favorite,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            onlyFavoriteSpinner.adapter = adapter
        }


        recipeRecycleAdapter = RecipesListRecycleAdapter(this, ::goToRecipe)

        val recipeRecycleView = findViewById<RecyclerView>(R.id.recipes_recycle_view)
        recipeRecycleView.apply {
            adapter = recipeRecycleAdapter
            layoutManager = LinearLayoutManager(this@FilterRecipesActivity)
            setHasFixedSize(true)
        }

        //Pobieranie listy przepisów z bazy danych
        recipeViewModel = ViewModelProviders.of(this@FilterRecipesActivity).get(RecipeViewModel::class.java)

        val filterRecipesButton = findViewById<Button>(R.id.start_filtering_button)
        filterRecipesButton.setOnClickListener {
            val mealTypeChosen = mealTypeSpinner.selectedItem.toString()
            val dishTypeChosen = dishTypeSpinner.selectedItem.toString()
            val timeToPrepareChosen = timeToPrepareSpinner.selectedItem.toString()
            val onlyFavoriteChosen = onlyFavoriteSpinner.selectedItem.toString()

            Log.d("TAG", "Wybrane filtry: $mealTypeChosen $dishTypeChosen $timeToPrepareChosen $onlyFavoriteChosen")

            recipeViewModel.getFilteredRecipes(mealTypeChosen, dishTypeChosen, timeToPrepareChosen, onlyFavoriteChosen)
        }

        recipeViewModel.currentRecipeList.observe(this@FilterRecipesActivity, Observer {
            recipeRecycleAdapter.setRecipeList(it)
        })

    }

    /**
     * Funkcja, która odsyła do wybranego przepisu
     */
    private fun goToRecipe(recipeId: String, recipeName: String) {
        val intent = Intent(this, RecipeActivity::class.java)
            .apply {
                putExtra(ChosenListActivity.EXTRA_RECIPE_ID, recipeId)
                putExtra(ChosenListActivity.EXTRA_RECIPE_NAME, recipeName)
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