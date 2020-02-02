package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.*
import com.pwinkler.inzapp.fragments.AddShoppingListDialogFragment
import com.pwinkler.inzapp.fragments.SendShoppingListDialogFragment
import com.pwinkler.inzapp.helpers.DpSize
import com.pwinkler.inzapp.models.ShoppingList
import com.pwinkler.inzapp.models.User
import com.pwinkler.inzapp.viewmodels.ShoppingListViewModel

class ShoppingListActivity: AppCompatActivity(), AddShoppingListDialogFragment.ModalListener, SendShoppingListDialogFragment.ModalListener {

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val collectionPath = "/shopping_lists"
    private lateinit var dpSize: DpSize

    lateinit var shoppingListViewModel: ShoppingListViewModel

    private var currentList : ShoppingList? = null

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

        dpSize = DpSize(this@ShoppingListActivity)

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
                val intent = Intent(this@ShoppingListActivity, FilterRecipesActivity::class.java)
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

        shoppingListViewModel = ViewModelProviders.of(this@ShoppingListActivity).get(ShoppingListViewModel::class.java)
        shoppingListViewModel.getUserShoppingList()

        val shoppingListName = findViewById<TextView>(R.id.shopping_list_name_text_view)
        val shoppingListContainer = findViewById<LinearLayout>(R.id.list_items_container)

        val shoppingListReference = db.collection(collectionPath)

        var checkStatus = false

        shoppingListReference.whereArrayContains("users", fbAuth.currentUser?.uid ?: "").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val shoppingListId = document.id
                    Log.d("TAG", "shoppingListID: $shoppingListId")
                    val shoppingListDocument = shoppingListReference.document(shoppingListId)

                    shoppingListDocument.get()
                        .addOnSuccessListener { document2 ->
                            if (document2 != null) {
                                shoppingListName?.text = document2.getString("name")

                                val items = document2["items"] as? ArrayList<String>

                                val activity = this@ShoppingListActivity
                                for (i in 0 until (items?.size ?: 0)) {

                                    val itemsContainerVertical = LinearLayout(activity).apply {
                                        orientation = LinearLayout.VERTICAL
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    }

                                    val itemsContainerHorizontal = LinearLayout(activity).apply {
                                        orientation = LinearLayout.HORIZONTAL
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                    }

                                    val itemCheckBox = CheckBox(activity).apply {
                                        text = items?.get(i) ?: ""
                                        textSize = 18F
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        (layoutParams as LinearLayout.LayoutParams).setMargins(0, 16, 0, 16)
                                    }

                                    itemsContainerHorizontal.addView(itemCheckBox)

                                    val separator2 = View(activity).apply {
                                        setBackgroundColor(Color.parseColor("#3A0A0A0A"))
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            dpSize.dpToPx(1)
                                        )
                                    }

                                    itemsContainerVertical.addView(itemsContainerHorizontal)
                                    itemsContainerVertical.addView(separator2)
                                    shoppingListContainer.addView(itemsContainerVertical)

                                    itemCheckBox.setOnCheckedChangeListener { view, isChecked ->
                                        if (isChecked) {
                                            checkStatus = true
                                            view.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                            Log.d("TAG", "checkbox: $isChecked")
                                        } else {
                                            checkStatus = false
                                            view.paintFlags = 0
                                        }
                                    }
                                }
                            }
                        }

                    shoppingListViewModel.currentShoppingList.observe(this@ShoppingListActivity, Observer {
                        currentList = it.first { shoppingList -> shoppingList.id == shoppingListId }
                    })
                }

            }

            .addOnFailureListener {
                Toast.makeText(application, "Pobieranie listy zakupów nie powiodło się", Toast.LENGTH_LONG).show()
            }


        val newShoppingListFAB = findViewById<FloatingActionButton>(R.id.add_shopping_list_fab)
        newShoppingListFAB.setOnClickListener {
            showAddShoppingListDialog()
        }
    }

    private fun deleteShoppingList(id: ShoppingList?) {
        shoppingListViewModel.deleteShoppingList(id)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
        this.recreate()
    }

    private fun showAddShoppingListDialog() {
        val dialog = AddShoppingListDialogFragment()
        dialog.show(supportFragmentManager, "AddShoppingListDialogFragment")
    }

    override fun onDialogPositiveClick(name: String, items: ArrayList<String>) {
        shoppingListViewModel.addShoppingList(name, items)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
        this.recreate()
    }

    private fun showSendShoppingListDialog() {
        val dialog = SendShoppingListDialogFragment()
        dialog.show(supportFragmentManager, "SendShoppingListDialogFragment")
    }

    override fun onSendShoppingListPositiveClick(user: User) {
        shoppingListViewModel.inviteUserToShoppingList(user, currentList)
    }

    /**
     * Dodawanie ikonek do bottom app bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shopping_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigationDrawer.openDrawer(GravityCompat.START)
            R.id.app_bar_add_a_person -> showSendShoppingListDialog()
            R.id.app_bar_remove_items_from_list -> deleteShoppingList(currentList)
        }
        return super.onOptionsItemSelected(item)
    }

}