package com.pwinkler.inzapp.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pwinkler.inzapp.models.Recipe
import com.pwinkler.inzapp.models.ShoppingList
import com.pwinkler.inzapp.models.User
import java.lang.NullPointerException

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()
    private val collectionPath = "/shopping_lists"
    private val productCollectionPath = "/products"
    private val shoppingListCollection = db.collection(collectionPath)
    private val productCollection = db.collection(productCollectionPath)
    private var shoppingList: ShoppingList? = null

    val currentShoppingList = MutableLiveData<List<ShoppingList>>()
    val currentInviteList = MutableLiveData<List<String>>()

    /**
     * Funkcja sprawdza czy użytkownik, który chce stworzyć listę, posiada już
     * listę zakupów. Jeżeli tak, dodanie listy zakupów sprawi, że dodadzą się
     * kolejne pozycje na liście. Jeżeli użytkownik chce stworzyć listę od nowa
     * najpierw musi wyczyścić listę, klikając w ikonę kosza.
     *
     */
    fun addShoppingList(name: String, items: ArrayList<String>) {

        shoppingListCollection.whereArrayContains("users", fbAuth.currentUser?.uid ?: "").get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val shoppingListReference = db.collection(collectionPath).document()
                    val updatedShoppingList =
                        ShoppingList(shoppingListReference.id, name, items, null)

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
                } else {
                    for (document in documents) {
                        Log.d("TAG", "document z addShoppingList: $document")
                        val shoppingListId = document.id
                        val data = HashMap<String, Any>()
                        val currentItems = (shoppingList?.items ?: arrayListOf()) as MutableList<String>

                        data["items"] = currentItems.apply {
                            for (i in 0 until items.size) {
                                add(items[i])
                                db.collection(collectionPath)
                                    .document(shoppingListId)
                                    .update("items", FieldValue.arrayUnion(items[i]))
                            }
                        }
                    }
                }
            }
    }

    /**
     * Funkcja, która usuwa listę zakupów użytkownika, jeżeli liczba użytkowników
     * danej listy wynosi 1. Jeżeli daną listę posiada więcej niż jednego użytkownika,
     * funkcja usunie id aktualnego użytkownika z tablicy users.
     */
    fun deleteShoppingList(shoppingListId: ShoppingList?) {
        shoppingListCollection.whereArrayContains("users", fbAuth.currentUser?.uid ?: "").get()
            .addOnSuccessListener {
                val documentId = shoppingListId!!.id
                val userId = fbAuth.currentUser?.uid
                val data = HashMap<String, Any>()
                val currentUsers = (shoppingListId.users ?: listOf()) as MutableList
                if (currentUsers.count() == 1) {
                    Log.d("TAG", "currentUsers.count: ${currentUsers.count()}")
                    shoppingListCollection.document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("TAG", "DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error deleting document", e
                            )
                        }
                } else {
                    data["users"] = currentUsers.apply {
                        shoppingListCollection
                            .document(documentId)
                            .update("users", FieldValue.arrayRemove(userId))
                    }
                }
            }
    }

    /**
     * Funkcja, która dodaje składniki wybranego przepisu
     * do listy zakupów
     */
    fun addIngredientsFromRecipeToShoppingList(ingredientsList: ArrayList<String>) {
        val productsList = arrayListOf<String>()
        for (i in 0 until ingredientsList.size) {
            val productReference = productCollection.document(ingredientsList[i])
            productReference.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val productName = document.getString("name")
                        val productQuantity = document.getString("quantity")
                        val productUnit = document.getString("unit")
                        val product = "$productName $productQuantity $productUnit"
                        productsList.add(product)
                    }
                    //addShoppingList("Produkty z przepisu", productsList)
                }
        }
        addShoppingList("Produkty z przepisu", productsList)
    }

    /**
     * Funkcja wyszukuje listę zakupów, którą posiada dany użytkownik
     * pobiera ją i zwraca jej wartości
     */
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

    /**
     * Funkcja zaprasza użytkownika do danej listy zakupów
     */
    fun inviteUserToShoppingList(user: User, shoppingList: ShoppingList?) {
        updateUserInviteList(user, shoppingList?.id)
            .addOnSuccessListener {
                addUserToShoppingList(user, shoppingList)
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Nie można wysłać listy zakupów", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Funkcja aktualizuje listę zaproszeń odbiorcy
     */
    private fun updateUserInviteList(user: User, shoppingListId: String?) : Task<Void> {
        val data = HashMap<String, Any>()
        val invites = user.invites as MutableList
        Log.d("TAG", "invites z updateUserInviteList: $invites")
        data["invites"] = invites.apply {
            add(shoppingListId ?: "")
        }

        return db.collection("users")
            .document(user.uid)
            .update(data)
    }

    /**
     * Funkcja dodaje nowego użytkownika do przepisu
     */
    private fun addUserToShoppingList(user: User, shoppingList: ShoppingList?) {
        val data = HashMap<String, Any>()
        val users = (shoppingList?.users ?: listOf()) as MutableList
        Log.d("TAG", "users z addUserToShoppingList: $users")
        data["users"] = users.apply {
            add(user.uid)
        }

        db.collection(collectionPath)
            .document(shoppingList?.id ?: "")
            .update(data)
    }

    /**
     * Funkcja, która bierze wszystkie zaproszenia użytkownika
     * i na końcu wywołuje funkcję deleteInvites,  która je usuwa
     */
    fun getInvites() {
        val uid = fbAuth.currentUser?.uid ?: return
        Log.d("TAG", "Usuwanko: " + fbAuth.currentUser?.uid.toString())
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val data = it.data ?: throw NullPointerException()
                Log.d("TAG", "it.data: $data")
                val invites = (data["invites"] ?: throw NoSuchFieldException()) as ArrayList<String>
                Log.d("TAG", "invites z getInvites: $invites")
                currentInviteList.postValue(invites)
                deleteInvites(uid)
            }
    }

    /**
     * Funkcja, która usuwa z bazy wszystkie zaproszenia użytkownika
     */
    private fun deleteInvites(uid: String) {
        val data = HashMap<String, Any>()
        data["invites"] = listOf("")

        db.collection("users")
            .document(uid)
            .update(data)
    }
}