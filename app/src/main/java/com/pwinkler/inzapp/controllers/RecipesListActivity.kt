package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pwinkler.inzapp.R
import com.pwinkler.inzapp.adapters.RecipesListRecycleAdapter
import com.pwinkler.inzapp.models.Recipe

class RecipesListActivity: AppCompatActivity() {

    lateinit var recipeRecycleAdapter: RecipesListRecycleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_recipes_list)
        setSupportActionBar(findViewById(R.id.my_recipes_toolbar))

        recipeRecycleAdapter = RecipesListRecycleAdapter(this, ::goToRecipe)

        val recipeRecycleView = findViewById<RecyclerView>(R.id.recipes_recycle_view)
        recipeRecycleView.apply {
            adapter = recipeRecycleAdapter
            layoutManager = LinearLayoutManager(this@RecipesListActivity)
            setHasFixedSize(true)
        }
    }

    private fun getRecipeFromId(recipes: List<Recipe>, id: String): Recipe {
        return recipes.first { it.id == id }
    }

    private fun goToRecipe(recipeId: String, recipeName: String) {
        val intent = Intent(this, RecipeActivity::class.java)
            .apply {
                putExtra(EXTRA_RECIPE_ID, recipeId)
                putExtra(EXTRA_RECIPE_NAME, recipeName)
            }
        startActivity(intent)
    }

    companion object {
        val EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID"
        val EXTRA_RECIPE_NAME = "EXTRA_RECIPE_NAME"
    }
}