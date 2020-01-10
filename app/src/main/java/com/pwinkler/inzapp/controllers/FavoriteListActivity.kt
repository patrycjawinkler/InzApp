package com.pwinkler.inzapp.controllers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.adapters.RecipesListRecycleAdapter
import com.pwinkler.inzapp.models.Recipe
import com.pwinkler.inzapp.viewmodels.RecipeViewModel

class FavoriteListActivity : AppCompatActivity() {

    private lateinit var recipeRecycleAdapter: RecipesListRecycleAdapter
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var navigationView: NavigationView
    private lateinit var navigationDrawer: DrawerLayout

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser == null) {
            val intent = Intent(this@FavoriteListActivity, LoginActivity::class.java)
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
        setContentView(R.layout.favorite_recipes_list)

        setSupportActionBar(findViewById(R.id.my_favorite_recipes_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.title = "Ulubione"

        navigationDrawer = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.apply{
            setHeader(fbAuth.currentUser?.email)
            setHeaderUsername(fbAuth.currentUser?.displayName)
            setLogoutAction {
                fbAuth.signOut()
            }
            setRecipeListAction {
                val intent = Intent(this@FavoriteListActivity, RecipesListActivity::class.java)
                startActivity(intent)
            }
            setShoppingListAction {
                val intent = Intent(this@FavoriteListActivity, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            setRandomRecipeAction {
                val intent = Intent(this@FavoriteListActivity, RandomRecipeActivity::class.java)
                startActivity(intent)
            }
            setGiveRecipeAction {
                val intent = Intent(this@FavoriteListActivity, ProposeRecipeActivity::class.java)
                startActivity(intent)
            }
            setFavoriteRecipesAction {
                val intent = Intent(this@FavoriteListActivity, FavoriteListActivity::class.java)
                startActivity(intent)
            }
            setChosenRecipesAction {
                val intent = Intent(this@FavoriteListActivity, ChosenListActivity::class.java)
                startActivity(intent)
            }
        }

        recipeRecycleAdapter = RecipesListRecycleAdapter(this, ::goToRecipe)

        val recipeRecycleView = findViewById<RecyclerView>(R.id.favorite_recipes_recycle_view)
        recipeRecycleView.apply {
            adapter = recipeRecycleAdapter
            layoutManager = LinearLayoutManager(this@FavoriteListActivity)
            setHasFixedSize(true)
        }

        //Pobieranie listy przepisów z bazy danych
        recipeViewModel = ViewModelProviders.of(this@FavoriteListActivity).get(RecipeViewModel::class.java)
        recipeViewModel.getAllFavoriteUserRecipes()

        recipeViewModel.currentRecipeList.observe(this@FavoriteListActivity, Observer {
            recipeRecycleAdapter.setRecipeList(it)
            showInviteNotification()
        })
    }
    /**
     * Funkcja, która odsyła do wybranego przepisu
     */
    private fun goToRecipe(recipeId: String, recipeName: String) {
        val intent = Intent(this, RecipeActivity::class.java)
            .apply {
                putExtra(EXTRA_RECIPE_ID, recipeId)
                putExtra(EXTRA_RECIPE_NAME, recipeName)
            }
        startActivity(intent)
    }

    private fun showInviteNotification() {
        val recipes = recipeViewModel.currentRecipeList.value ?: return
        val invites = recipeViewModel.currentInviteList.value ?: return
        invites
            .filter {it != " "}
            .forEach{
                val recipe = getRecipeFromId(recipes, it)
                showNotification("Otrzymałeś nowy przepis: ", recipe.name)
            }
    }

    private fun getRecipeFromId(recipes: List<Recipe>, id: String): Recipe {
        return recipes.first { it.id == id }
    }

    private fun showNotification(title: String, msg: String) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("YOUR_CHANNEL_ID", "YOUR_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "YOUR_NOTIFICATION_CHANNEL_DESCRIPTION"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.mipmap.ic_launcher) //ustaw ikonkę aplikacji
            .setContentTitle(title) //ustaw tytuł powiadomienia
            .setContentText(msg) //ustaw wiadomość - nazwę przepisu
            .setAutoCancel(true) //usuń powiadomienie po kliknięciu
        mNotificationManager.notify(0, mBuilder.build())
    }

    /**
     * Funkcja, która tworzy ikonki na toolbarze
     * i podpina narzędzie szukania
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.recipe_list_toolbar_menu, menu)
        val mSearch = menu.findItem(R.id.app_bar_search)
        val mSearchView = mSearch.actionView as SearchView
        mSearchView.queryHint = "Szukaj..."
        mSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Funkcja, która iteruje po przepisach
     * i zwraca tylko te zawierające query
     */
    private fun filterRecipes(query: String?){
        val filteredRecipes = recipeViewModel.currentRecipeList.value
            ?.filter {
                doesRecipeContainSearchQuery(it, query)
            }
        recipeRecycleAdapter.setRecipeList(filteredRecipes ?: listOf())
    }

    /**
     * Sprawdza czy w nazwie przepisu występuje query
     *
     */
    private fun doesRecipeContainSearchQuery(recipe: Recipe, query: String?): Boolean {
        return (recipe.name.contains(query ?: "", true))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigationDrawer.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID"
        val EXTRA_RECIPE_NAME = "EXTRA_RECIPE_NAME"
    }
}