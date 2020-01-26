package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.fragments.AddShoppingListDialogFragment
import com.pwinkler.inzapp.helpers.DpSize
import com.pwinkler.inzapp.viewmodels.RecipeViewModel
import com.pwinkler.inzapp.viewmodels.ShoppingListViewModel

class ShoppingListActivity: AppCompatActivity(), AddShoppingListDialogFragment.ModalListener {

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var shoppingListViewModel: ShoppingListViewModel
    private lateinit var navigationView: NavigationView
    private lateinit var navigationDrawer: DrawerLayout

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser == null) {
            val intent = Intent(this@ShoppingListActivity, LoginActivity::class.java)
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
        setContentView(R.layout.shopping_list)

        shoppingListViewModel = ViewModelProviders.of(this@ShoppingListActivity).get(ShoppingListViewModel::class.java)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.title = "Lista zakupów"

        navigationDrawer = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setCheckedItem(R.id.nav_shopping_list)
        navigationView.apply {
            setHeader(fbAuth.currentUser?.email)
            setHeaderUsername(fbAuth.currentUser?.displayName)
            setLogoutAction {
                fbAuth.signOut()
            }
            setRecipeListAction {
                val intent = Intent(this@ShoppingListActivity, RecipesListActivity::class.java)
                startActivity(intent)
            }
            setShoppingListAction {
                val intent = Intent(this@ShoppingListActivity, ShoppingListActivity::class.java)
                startActivity(intent)
            }
            setRandomRecipeAction {
                val intent = Intent(this@ShoppingListActivity, RandomRecipeActivity::class.java)
                startActivity(intent)
            }
            setGiveRecipeAction {
                val intent = Intent(this@ShoppingListActivity, ProposeRecipeActivity::class.java)
                startActivity(intent)
            }
            setFavoriteRecipesAction {
                val intent = Intent(this@ShoppingListActivity, FavoriteListActivity::class.java)
                startActivity(intent)
            }
            setChosenRecipesAction {
                val intent = Intent(this@ShoppingListActivity, ChosenListActivity::class.java)
                startActivity(intent)
            }
        }

        val newShoppingListFAB = findViewById<FloatingActionButton>(R.id.add_shopping_list_fab)
        newShoppingListFAB.setOnClickListener {
            showAddShoppingListDialog()
        }
    }

    private fun showAddShoppingListDialog() {
        val dialog = AddShoppingListDialogFragment()
        dialog.show(supportFragmentManager, "AddShoppingListDialogFragment")
    }

    override fun onDialogPositiveClick(name: String, items: ArrayList<String>) {
        shoppingListViewModel.addShoppingList(name, items)
    }
}