package com.pwinkler.inzapp.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.pwinkler.inzapp.models.Recipe
import java.lang.NullPointerException

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()

    private val collectionPath = "/recipes"

    val currentRecipeList = MutableLiveData<List<Recipe>>()
    val currentInviteList = MutableLiveData<ArrayList<String>>()

    fun getAllUserRecipes() {

        val recipeCollection = db.collection(collectionPath)

        recipeCollection.whereArrayContains("users", fbAuth.currentUser?.uid ?: "").get()
            .addOnSuccessListener { documents ->
                currentRecipeList.postValue(
                    documents.map { recipe ->
                        val data = recipe.data
                        val id = recipe.id
                        val name = data["name"] ?: throw NoSuchFieldException()
                        val description = data["description"] ?: throw NoSuchFieldException()
                        val image = data["image_id"] ?: ""
                        val timeToPrepare = data["time_to_prepare"] ?: throw NoSuchFieldException()
                        val mealType = data["meal_type"] ?: throw NoSuchFieldException()
                        val dishType = data["dish_type"] ?: throw NoSuchFieldException()
                        val ingredients = data["ingredients"] ?: arrayListOf("")
                        val users = data["users"] ?: throw NoSuchFieldException()

                        Recipe(
                            id,
                            name as String,
                            description as String,
                            image as String,
                            timeToPrepare as String,
                            mealType as String,
                            dishType as String,
                            ingredients as ArrayList<String>,
                            users as List<String>
                        )
                    }
                )
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Pobieranie z bazy nie powiodło się", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Funkcja, która bierze wszystkie zaproszenia użytkownika
     * i na końcu je usuwa
     */
    fun getInvites() {
        val uid = fbAuth.currentUser?.uid ?: return
        Log.d("TAG", "Usuwanko: " + fbAuth.currentUser?.uid.toString())
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val data = it.data ?: throw NullPointerException()
                val invites = (data["invites"] ?: throw NoSuchFieldException()) as ArrayList<String>
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

    /**
     * Funkcja, która dodaje przespis do bazy
     */
    fun addRecipe(name: String, description: String, image_id: String, time_to_prepare: String,
                  meal_type: String, dish_type: String, ingredients: ArrayList<String>) {

        //Utwórz nowy dokument i pobierz referencje (id jest tworzone automatycznie)
        val recipeReference = db.collection(collectionPath).document()

        val updatedRecipe = Recipe(recipeReference.id, name, description, image_id, time_to_prepare,
            meal_type, dish_type, ingredients, null)

        recipeReference.set(
            HashMap<String, Any>().apply {
                this["id"] = updatedRecipe.id
                this["name"] = updatedRecipe.name
                this["description"] = updatedRecipe.description
                this["image_id"] = updatedRecipe.image_id
                this["time_to_prepare"] = updatedRecipe.time_to_prepare
                this["meal_type"] = updatedRecipe.meal_type
                this["dish_type"] = updatedRecipe.dish_type
                this["ingredients"] = updatedRecipe.ingredients
                this["users"] = listOf(fbAuth.currentUser!!.uid)
            },
            SetOptions.merge()
        )

        val oldRecipeList = currentRecipeList.value
        val newRecipeList: List<Recipe>
        newRecipeList = if(oldRecipeList != null) {
            oldRecipeList + listOf(updatedRecipe)
        } else {
            listOf(updatedRecipe)
        }

        currentRecipeList.postValue(newRecipeList)
    }
}