package com.pwinkler.inzapp.fragments

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pwinkler.inzapp.R
import com.pwinkler.inzapp.viewmodels.RecipeViewModel
import java.lang.ClassCastException

class AddRecipeDialogFragment : DialogFragment() {

    private lateinit var listener: ModalListener
    private lateinit var dialogView: View
    private var ingredients = 1


    /**
     * Interfejs, który RecipesListActivity musi zaimplementować,
     * aby mogło z niego korzystać
     */

    interface ModalListener {
        fun onDialogPositiveClick(
            name: String,
            description: String,
            time_to_prepare: String,
            meal_type: String,
            dish_type: String,
            ingredients: ArrayList<String>
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        /**Funkcja sprawdza czy Activity implementuje listener,
         * jeśli nie to rzuć exception
         */
        try {
            listener = context as ModalListener
        } catch (exception: ClassCastException) {
            throw ClassCastException(
                "$context musi implemenować AddCardDialogListener"
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity, R.style.FullScreenDialogTheme)

            val inflater = activity.layoutInflater
            dialogView = inflater.inflate(R.layout.add_recipe_modal, null)

            val addNewIngredient = dialogView.findViewById<ImageButton>(R.id.add_ingredient_button)
            addNewIngredient.setOnClickListener {
                addAnotherIngredient()
            }
            addNewIngredient.setOnLongClickListener {
                Toast.makeText(activity, "Kliknij, aby dodać nowy składnik", Toast.LENGTH_LONG)
                    .show()
                true
            }

            val mealTypeSpinner =
                dialogView.findViewById<Spinner>(R.id.meal_type_spinner)
            val dishTypeSpinner =
                dialogView.findViewById<Spinner>(R.id.dish_type_spinner)
            val timeToPrepareSpinner =
                dialogView.findViewById<Spinner>(R.id.time_to_prepare_spinner)

            /**
             * Podpięcie opcji wyboru pod spinnery
             * simple_spinner_item i simple_spinner_dropdown_item to layouty wbudowane
             */

            ArrayAdapter.createFromResource(
                activity,
                R.array.meal_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mealTypeSpinner.adapter = adapter
            }

            ArrayAdapter.createFromResource(
                activity,
                R.array.dish_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dishTypeSpinner.adapter = adapter
            }

            ArrayAdapter.createFromResource(
                activity,
                R.array.time_to_prepare,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                timeToPrepareSpinner.adapter = adapter
            }


            val dialog = builder.run {
                setView(dialogView)
                    .setPositiveButton("Dodaj przepis", null)
                    .setNegativeButton("Anuluj", null)
                create()
            }

            dialog.apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {
                        val recipeNameTextEdit =
                            dialogView.findViewById<EditText>(R.id.input_dish_name)
                        val ingredientContainer =
                            dialogView.findViewById<LinearLayout>(R.id.modal_ingredients_container)
                        val descriptionTextEdit =
                            dialogView.findViewById<EditText>(R.id.input_description)

                        val ingredientList = arrayListOf<String>()
                        for (i in 0 until ingredientContainer.childCount) {
                            val ingredient = ingredientContainer.getChildAt(i) as LinearLayout
                            val text = (ingredient.getChildAt(1) as EditText).text.toString()
                            if (text.isNotBlank()) {
                                ingredientList.add(text)
                            }
                        }
                        if (recipeNameTextEdit.text.toString().isBlank() || descriptionTextEdit.text.toString().isBlank()) {
                            Toast.makeText(
                                activity,
                                "Proszę uzupełnić nazwę dania i opis przygotowania",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (ingredientList.size < 2) {
                            Toast.makeText(
                                activity,
                                "Proszę uzupełnić składniki",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            listener.onDialogPositiveClick(
                                recipeNameTextEdit.text.toString(),
                                descriptionTextEdit.text.toString(),
                                timeToPrepareSpinner.selectedItem.toString(),
                                mealTypeSpinner.selectedItem.toString(),
                                dishTypeSpinner.selectedItem.toString(),
                                ingredientList
                            )
                            dialog.dismiss()
                        }
                    }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Funkcja, która umożliwia dodanie kolejnego składnika
     */
    fun addAnotherIngredient() {
        ingredients++

        if (ingredients > 20) {
            return
        }

        val container = dialogView.findViewById<LinearLayout>(R.id.modal_ingredients_container)
        val newIngredient = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val removeIcon = ImageButton(activity).apply {
            setImageResource(R.drawable.ic_delete_24dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        newIngredient.addView(removeIcon)

        val ingredientInput = EditText(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        newIngredient.addView(ingredientInput)

        container.addView(newIngredient)
    }
}
