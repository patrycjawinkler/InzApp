package com.pwinkler.inzapp.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.pwinkler.inzapp.models.Recipe
import com.pwinkler.inzapp.models.User
import java.lang.NullPointerException

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()
    private val fbAuth = FirebaseAuth.getInstance()

    private val collectionPath = "/recipes"

    val currentRecipeList = MutableLiveData<List<Recipe>>()
    val currentInviteList = MutableLiveData<List<String>>()

    private val recipeCollection = db.collection(collectionPath)

    /**
     * Funkcja, która pobiera z bazy wszystkie przepisy użytkownika
     */
    fun getAllUserRecipes() {
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
                        val favorite = data["favorite"] ?: ""
                        val chosen = data["chosen"] ?: ""
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
                            favorite as Boolean,
                            chosen as Boolean,
                            users as List<String>
                        )
                    }
                )
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Pobieranie wszystkich przepisów z bazy nie powiodło się", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Funkcja, która pobiera z bazy wszystkie ulubione przepisy użytkownika
     */
    fun getAllFavoriteUserRecipes() {
        recipeCollection.whereArrayContains("users", fbAuth.currentUser?.uid ?: "").whereEqualTo("favorite", true).get()
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
                        val favorite = data["favorite"] ?: ""
                        val chosen = data["chosen"] ?: ""
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
                            favorite as Boolean,
                            chosen as Boolean,
                            users as List<String>
                        )
                    }
                )
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Pobieranie ulubionych przepisów z bazy nie powiodło się", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Funkcja, która pobiera z bazy wszystkie ulubione przepisy użytkownika
     */
    fun getAllChosenUserRecipes() {
        recipeCollection.whereArrayContains("users", fbAuth.currentUser?.uid ?: "").whereEqualTo("chosen", true).get()
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
                        val favorite = data["favorite"] ?: ""
                        val chosen = data["chosen"] ?: ""
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
                            favorite as Boolean,
                            chosen as Boolean,
                            users as List<String>
                        )
                    }
                )
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Pobieranie wybranych przepisów z bazy nie powiodło się", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Funkcja, która dodaje przespis do bazy
     */
    fun addRecipe(name: String, description: String, image_id: String, time_to_prepare: String,
                  meal_type: String, dish_type: String, ingredients: ArrayList<String>, favorite: Boolean = false, chosen: Boolean = false) {

        //Utwórz nowy dokument i pobierz referencje (id jest tworzone automatycznie)
        val recipeReference = db.collection(collectionPath).document()

        val updatedRecipe = Recipe(recipeReference.id, name, description, image_id, time_to_prepare,
            meal_type, dish_type, ingredients, favorite, chosen, null)

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
                this["favorite"] = updatedRecipe.favorite
                this["chosen"] = updatedRecipe.chosen
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

    /**
     * Funkcja wysyła przepis do innego użytkownika
     */
    fun inviteUser(user: User, recipe: Recipe?) {
        updateUserInviteList(user, recipe?.id)
            .addOnSuccessListener {
                addUserToRecipe(user, recipe)
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Nie można wysłać przepisu", Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Funkcja aktualizuje listę zaproszeń użytkownika
     */
    private fun updateUserInviteList(user: User, recipeId: String?) : Task<Void> {
        val data = HashMap<String, Any>()
        val invites = user.invites as MutableList
        Log.d("TAG", "invites z updateUserInviteList: $invites")
        data["invites"] = invites.apply {
            add(recipeId ?: "")
        }

        return db.collection("users")
            .document(user.uid)
            .update(data)
    }

    /**
     * Funkcja dodaje nowego użytkownika do przepisu
     */
    private fun addUserToRecipe(user: User, recipe: Recipe?) {
        val data = HashMap<String, Any>()
        val users = (recipe?.users ?: listOf()) as MutableList
        Log.d("TAG", "users z addUserToProject: $users")
        data["users"] = users.apply {
            add(user.uid)
        }

        db.collection(collectionPath)
            .document(recipe?.id ?: "")
            .update(data)
    }
}