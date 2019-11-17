package com.pwinkler.inzapp.controllers

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pwinkler.inzapp.R

class AddRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_recipe_modal)

        val recipeNameTextEdit = findViewById<EditText>(R.id.input_dish_name)
        val mealTypeSpinner = findViewById<Spinner>(R.id.meal_type_spinner)
        val dishTypeSpinner = findViewById<Spinner>(R.id.dish_type_spinner)
        val timeToPrepareSpinner = findViewById<Spinner>(R.id.time_to_prepare_spinner)
        val ingredientTextEdit = findViewById<EditText>(R.id.input_ingredient)
        val descriptionTextEdit = findViewById<EditText>(R.id.input_description)

        /**
         * Podpięcie opcji wyboru pod spinnery
         * simple_spinner_item i simple_spinner_dropdown_item
         * to layouty wbudowane
         */

        //Spinnery

        //region

        ArrayAdapter.createFromResource(
            this,
            R.array.meal_types,
            android.R.layout.simple_spinner_item
        ).also  { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mealTypeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.dish_types,
            android.R.layout.simple_spinner_item
        ).also  { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dishTypeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.time_to_prepare,
            android.R.layout.simple_spinner_item
        ).also  { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timeToPrepareSpinner.adapter = adapter
        }

         mealTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

             override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
             }

             override fun onNothingSelected(p0: AdapterView<*>?) {
                 TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
             }
         }

        dishTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }


        timeToPrepareSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        //endregion

        val addNewIngredient = findViewById<ImageButton>(R.id.add_ingredient_button)
        addNewIngredient.setOnClickListener {
            addAnotherIngredient()
        }
        addNewIngredient.setOnLongClickListener {
            Toast.makeText(this, "Kliknij, aby dodać nowy składnik", Toast.LENGTH_LONG).show()
            true
        }

        val addRecipeButton = findViewById<Button>(R.id.add_recipe_button)
        addRecipeButton.setOnClickListener {
            val name = recipeNameTextEdit.text.toString()
            val ingredient = ingredientTextEdit.text.toString()
            val description = descriptionTextEdit.text.toString()
            if (name.isEmpty() || ingredient.isEmpty() || description.isEmpty()){
                Toast.makeText(this, "Proszę uzupełnić brakujące pola", Toast.LENGTH_LONG).show()
            } else {
                //TODO
            }
        }
    }

    private fun addAnotherIngredient() {

    }
}
