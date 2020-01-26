package com.pwinkler.inzapp.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pwinkler.inzapp.models.ShoppingList

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()
    private val collectionPath = "/shopping_lists"
    private val shoppingListCollection = db.collection(collectionPath)

    val currentShoppingList = MutableLiveData<List<ShoppingList>>()
    val currentInviteList = MutableLiveData<List<String>>()

    fun addShoppingList(name: String, items: ArrayList<String>) {

        val shoppingListReference = db.collection(collectionPath).document()

        val updatedShoppingList = ShoppingList(shoppingListReference.id, name, items, null)

        shoppingListReference.set(
            HashMap<String, Any>().apply {
                this["id"] = updatedShoppingList.id
                this["name"] = updatedShoppingList.name
                this["items"] = updatedShoppingList.items
                this["users"] = listOf(fbAuth.currentUser!!.uid)
            },
            SetOptions.merge()
        )

        val oldShoppingList = currentShoppingList.value
        val newShoppingList: List<ShoppingList>
        newShoppingList = if (oldShoppingList != null) {
            oldShoppingList + listOf(updatedShoppingList)
        } else {
            listOf(updatedShoppingList)
        }

        currentShoppingList.postValue(newShoppingList)
    }

    fun getUserShoppingList() {
        shoppingListCollection.whereArrayContains("users", fbAuth.currentUser?.uid ?: "").get()
            .addOnSuccessListener { document ->
                currentShoppingList.postValue(
                    document.map { shoppingList ->
                        val data = shoppingList.data
                        val id = shoppingList.id
                        val name = data["name"] ?: throw NoSuchFieldException()
                        val items = data["items"] ?: arrayListOf("")
                        val users = data["users"] ?: throw NoSuchFieldException()

                        ShoppingList(
                            id,
                            name as String,
                            items as ArrayList<String>,
                            users as List<String>
                        )
                    }
                )
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Pobieranie listy zakupów nie powiodło się", Toast.LENGTH_LONG).show()
            }
    }

}