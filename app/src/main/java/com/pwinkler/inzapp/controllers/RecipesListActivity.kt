package com.pwinkler.inzapp.controllers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.adapters.RecipesListRecycleAdapter
import com.pwinkler.inzapp.fragments.AddRecipeDialogFragment
import com.pwinkler.inzapp.models.Recipe
import com.pwinkler.inzapp.viewmodels.RecipeViewModel
import kotlin.system.exitProcess

class RecipesListActivity: AppCompatActivity(), AddRecipeDialogFragment.ModalListener {

    lateinit var recipeRecycleAdapter: RecipesListRecycleAdapter
    lateinit var recipeViewModel: RecipeViewModel
    private lateinit var navigationView: NavigationView

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser == null) {
            val intent = Intent(this@RecipesListActivity, LoginActivity::class.java)
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

    //Funkcja onBackPressed niepotrzebna, ponieważ będziemy się cofać do MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_recipes_list)
        setSupportActionBar(findViewById(R.id.my_recipes_toolbar))

        navigationView = findViewById(R.id.navigation_view)
        navigationView.apply{
            setHeader(fbAuth.currentUser?.email)
            setLogoutAction {
                fbAuth.signOut()
            }
            setRecipeListAction {
                val intent = Intent(this@RecipesListActivity, RecipesListActivity::class.java)
                startActivity(intent)
            }
            setShoppingListAction {
                val intent = Intent(this@RecipesListActivity, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            setRandomRecipeAction {
                val intent = Intent(this@RecipesListActivity, RandomRecipeActivity::class.java)
                startActivity(intent)
            }
            setGiveRecipeAction {
                val intent = Intent(this@RecipesListActivity, ProposeRecipeActivity::class.java)
                startActivity(intent)
            }
            setFavoriteRecipesAction {
                val intent = Intent(this@RecipesListActivity, FavoriteListActivity::class.java)
                startActivity(intent)
            }
            setChosenRecipesAction {
                val intent = Intent(this@RecipesListActivity, ChosenListActivity::class.java)
                startActivity(intent)
            }
        }

        recipeRecycleAdapter = RecipesListRecycleAdapter(this, ::goToRecipe)

        val recipeRecycleView = findViewById<RecyclerView>(R.id.recipes_recycle_view)
        recipeRecycleView.apply {
            adapter = recipeRecycleAdapter
            layoutManager = LinearLayoutManager(this@RecipesListActivity)
            setHasFixedSize(true)
        }

        //Pobieranie listy przepisów z bazy danych
        recipeViewModel = ViewModelProviders.of(this@RecipesListActivity).get(RecipeViewModel::class.java)
        recipeViewModel.getAllUserRecipes()

        recipeViewModel.currentRecipeList.observe(this@RecipesListActivity, Observer {
            recipeRecycleAdapter.setRecipeList(it)
            showInviteNotification()
        })

        /**
        recipeViewModel.getInvites()
        recipeViewModel.currentInviteList.observe(this@RecipesListActivity, Observer {
            showInviteNotification()
        })
        */

        val recipeFAB = findViewById<FloatingActionButton>(R.id.add_recipes_fab)
        recipeFAB.setOnClickListener{
            showAddRecipeDialog()
        }
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

    private fun goToRecipe(recipeId: String, recipeName: String) {
        val intent = Intent(this, RecipeActivity::class.java)
            .apply {
                putExtra(EXTRA_RECIPE_ID, recipeId)
                putExtra(EXTRA_RECIPE_NAME, recipeName)
            }
        startActivity(intent)
    }

    /**
     * Funkcja, która pokazuje dialog tworzenia projektu,
     * po naciśnięciu FABa
     */
    private fun showAddRecipeDialog() {
        val dialog = AddRecipeDialogFragment()
        dialog.show(supportFragmentManager, "AddRecipeDialogFragment")
    }

    override fun onDialogPositiveClick(
        name: String,
        description: String,
        image_id: String,
        time_to_prepare: String,
        meal_type: String,
        dish_type: String,
        ingredients: ArrayList<String>) {

        recipeViewModel.addRecipe(name, description, image_id, time_to_prepare, meal_type, dish_type, ingredients)
    }

    /**
     * Const używane w celu identyfkacji przesyłanych danych za pomocą intent.extra
     */

    companion object {
        val EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID"
        val EXTRA_RECIPE_NAME = "EXTRA_RECIPE_NAME"
    }
}