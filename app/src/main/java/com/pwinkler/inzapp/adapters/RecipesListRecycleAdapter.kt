package com.pwinkler.inzapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pwinkler.inzapp.R
import com.pwinkler.inzapp.models.Recipe

/**
 * ViewHolder jest używany, aby przechowywać obiekty w pamięci,
 * w celu zaoszczędzenia pamięci
 * Adapter tworzy nowe obiekty w widoku i zapełnia ViewHolder danymi,
 * a także zwraca informacje na temat danych
 */

class RecipesListRecycleAdapter(context: Context, val goToRecipe: (String, String) -> Unit)
    : RecyclerView.Adapter<RecipesListRecycleAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var recipesList = emptyList<Recipe>()

    //Zwraca liczbę elementów w liście
    override fun getItemCount(): Int {
        return recipesList.count()
    }

    //Zapełnia widok kartami przepisów
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recipe_card, parent, false))
    }

    //Podpina każdy przepis z listy do widoku
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipesList[position])
    }

    /**
     * Wewnętrzna klasa, która tworzy widok przepisu,
     * ustawia dane z bazy i umożliwia przejście do danego przepisu
     */

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //Tworzenie referencji do elementów layoutu

        val recipeListItemContainer = itemView.findViewById<ConstraintLayout>(R.id.recipe_list_item_container)
        //val dishImageView = itemView.findViewById<ImageView>(R.id.dish_image)
        val dishNameTextView = itemView.findViewById<TextView>(R.id.dish_name)
        val timeToPrepareTextView = itemView.findViewById<TextView>(R.id.time_to_prepare)
        val dishTypeTextView = itemView.findViewById<TextView>(R.id.dish_type_text)

        fun bind(recipe: Recipe) {

            //Ustawianie wyglądu i nadawanie im funkcjonalności

            //dishImageView?.setImageResource(recipe.imageId)
            dishNameTextView?.text = recipe.name
            timeToPrepareTextView?.text = recipe.time_to_prepare
            dishTypeTextView?.text = recipe.dish_type

            recipeListItemContainer.setOnClickListener {
                goToRecipe(recipe.id, recipe.name)
            }
        }
    }

    /**
     *Funkcja odswieżająca adapter i ustawiająca nową listę przepisów
     */

    /**internal fun setRecipeList(recipeList: List<Recipe>){
        this@RecipesListRecycleAdapter.recipesList = recipesList
        notifyDataSetChanged()
    }
    */
}