package com.pwinkler.inzapp.controllers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.adapters.RecipesListRecycleAdapter
import com.pwinkler.inzapp.models.ShoppingList
import com.pwinkler.inzapp.viewmodels.ShoppingListViewModel
import kotlin.random.Random
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var navigationView : NavigationView
    lateinit var shoppingListViewModel : ShoppingListViewModel
    private lateinit var navigationDrawer: DrawerLayout

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

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
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
        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START)
        } else {
            finishAffinity()
            exitProcess(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationDrawer = findViewById(R.id.activity_main_drawer_layout)
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

        shoppingListViewModel = ViewModelProviders.of(this@MainActivity).get(ShoppingListViewModel::class.java)
        shoppingListViewModel.getUserShoppingList()

        shoppingListViewModel.currentShoppingList.observe(this@MainActivity, Observer {
            showNewShoppingListNotification()
        })

        shoppingListViewModel.getInvites()
        shoppingListViewModel.currentInviteList.observe(this@MainActivity, Observer {
            showNewShoppingListNotification()
        })

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

    private fun showNewShoppingListNotification() {
        val shoppingList = shoppingListViewModel.currentShoppingList.value ?: return
        val invites = shoppingListViewModel.currentInviteList.value ?: return
        invites
            .filter {it != ""}
            .forEach{
                val list = getShoppingListFromId(shoppingList, it)
                showNotification("Otrzymałeś listę zakupów: ", list.name)
            }
    }

    private fun getShoppingListFromId(shoppingLists: List<ShoppingList>, id: String): ShoppingList {
        return shoppingLists.first { it.id == id}
    }

    private fun showNotification(title: String, msg: String) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("YOUR_CHANNEL_ID", "YOUR_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "YOUR_NOTIFICATION_CHANNEL_DESCRIPTION"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)) //ustaw dużą ikonkę aplikacji
            .setSmallIcon(R.mipmap.ic_launcher) //ustaw małą ikonkę aplikacji
            .setContentTitle(title) //ustaw tytuł powiadomienia
            .setContentText(msg) //ustaw wiadomość - nazwę przepisu
            .setAutoCancel(true) //usuń powiadomienie po kliknięciu
        mNotificationManager.notify(0, mBuilder.build())
    }
}
