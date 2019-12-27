package com.pwinkler.inzapp.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.pwinkler.inzapp.R
import com.pwinkler.inzapp.helpers.DpSize
import com.pwinkler.inzapp.models.Product
import com.pwinkler.inzapp.viewmodels.ProductViewModel
import com.pwinkler.inzapp.viewmodels.RecipeViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_recipe_modal.view.*
import java.lang.ClassCastException
import java.util.*
import kotlin.collections.ArrayList

class AddRecipeDialogFragment : DialogFragment() {

    private var PICK_IMAGE_REQUST = 1

    private lateinit var listener: ModalListener
    private lateinit var dialogView: View
    private lateinit var mImageUri : Uri
    private lateinit var mIngredientId : String
    private lateinit var dpSize: DpSize

    private var ingredients = 1

    lateinit var storage : FirebaseStorage
    lateinit var storageReference : StorageReference

    private val db = FirebaseFirestore.getInstance()
    private val collectionPath = "/products"

    /**
     * Interfejs, który RecipesListActivity musi zaimplementować,
     * aby mogło z niego korzystać
     */

    interface ModalListener {
        fun onDialogPositiveClick(
            name: String,
            description: String,
            image_id: String,
            time_to_prepare: String,
            meal_type: String,
            dish_type: String,
            ingredients: ArrayList<String>
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dpSize = DpSize(context)

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

            //Stworzenie referencji do Firebase Storage
            storage = FirebaseStorage.getInstance()
            storageReference = storage.reference


            val addNewIngredient =
                dialogView.findViewById<ImageButton>(R.id.add_ingredient_button)
            val ingredientContainer =
                dialogView.findViewById<LinearLayout>(R.id.modal_ingredients_container)

            val ingredientList = arrayListOf<String>()

            addNewIngredient.setOnClickListener {
                for (i in 0 until ingredientContainer.childCount) {
                    val ingredientsLayoutVertical = ingredientContainer.getChildAt(i) as LinearLayout
                    val ingredientNameTextInput = (ingredientsLayoutVertical.getChildAt(i) as EditText).text.toString()
                    val detailsLayoutHorizontal = ingredientsLayoutVertical.getChildAt(1) as LinearLayout
                    val ingredientQuantityTextInput = (detailsLayoutHorizontal.getChildAt(1) as EditText).text.toString()
                    val ingredientUnitSpinner = (detailsLayoutHorizontal.getChildAt(2) as Spinner).selectedItem.toString()

                    if (ingredientNameTextInput.isBlank() || ingredientQuantityTextInput.isBlank() || ingredientUnitSpinner.isBlank()) {
                        Toast.makeText(activity, "Proszę uzupełnić szczegóły składników", Toast.LENGTH_LONG).show()
                    } else {
                        val productReference = db.collection(collectionPath).document()

                        val updatedProduct = Product(productReference.id, ingredientNameTextInput, ingredientQuantityTextInput, ingredientUnitSpinner)

                        productReference.set(
                            HashMap<String, Any>().apply {
                                this["id"] = updatedProduct.id
                                this["name"] = updatedProduct.name
                                this["quantity"] = updatedProduct.quantity
                                this["unit"] = updatedProduct.unit
                            },
                            SetOptions.merge()
                        )
                        ingredientList.add(productReference.id)
                    }
                    addViewsForNewIngredient()
                }
            }

            addNewIngredient.setOnLongClickListener {
                Toast.makeText(activity, "Kliknij, aby dodać nowy składnik", Toast.LENGTH_LONG).show()
                true
            }

            val addImage = dialogView.findViewById<ImageButton>(R.id.add_photo_image)
            addImage.setOnClickListener {
                openFileChooser()
            }
            addImage.setOnLongClickListener {
                Toast.makeText(activity, "Kliknij, aby dodać zdjęcie", Toast.LENGTH_LONG).show()
                true
            }

            val mealTypeSpinner =
                dialogView.findViewById<Spinner>(R.id.meal_type_spinner)
            val dishTypeSpinner =
                dialogView.findViewById<Spinner>(R.id.dish_type_spinner)
            val timeToPrepareSpinner =
                dialogView.findViewById<Spinner>(R.id.time_to_prepare_spinner)
            val unitSpinner =
                dialogView.findViewById<Spinner>(R.id.unit_spinner)

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

            ArrayAdapter.createFromResource(
                activity,
                R.array.units,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                unitSpinner.adapter = adapter
            }


            //utworzenie przycisków na ekranie modala

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
                        val descriptionTextEdit =
                            dialogView.findViewById<EditText>(R.id.input_description)

                        val photoUid = UUID.randomUUID().toString()
                        val imageRef = storageReference.child("images/" + photoUid)

                        imageRef.putFile(mImageUri).apply {
                            addOnSuccessListener {
                                if (recipeNameTextEdit.text.toString().isBlank() || descriptionTextEdit.text.toString().isBlank()) {
                                    Toast.makeText(
                                        activity,
                                        "Proszę uzupełnić brakujące pola",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (ingredientList.size < 1) {
                                    Toast.makeText(
                                        activity,
                                        "Proszę uzupełnić składniki",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    imageRef.downloadUrl.addOnSuccessListener {  uri ->
                                        listener.onDialogPositiveClick(
                                            recipeNameTextEdit.text.toString(),
                                            descriptionTextEdit.text.toString(),
                                            uri.toString(),
                                            timeToPrepareSpinner.selectedItem.toString(),
                                            mealTypeSpinner.selectedItem.toString(),
                                            dishTypeSpinner.selectedItem.toString(),
                                            ingredientList
                                        )
                                        dialog.dismiss()
                                    }
                                }
                            }
                        }
                    }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Funkcja, która umożliwia dodanie kolejnego składnika
     */
    private fun addViewsForNewIngredient() {
        ingredients++

        if (ingredients > 20) {
            return
        }

        val container = dialogView.findViewById<LinearLayout>(R.id.modal_ingredients_container)

        val ingredientsContainer = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val ingredientInput = EditText(activity).apply {
            hint = "Nazwa składnika"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val detailsContainer = LinearLayout(activity).apply {
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
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        detailsContainer.addView(removeIcon)

        val quantityInput = EditText(activity).apply {
            hint = "Ilość"
            inputType = 2
            layoutParams = LinearLayout.LayoutParams(
                dpSize.dpToPx(100),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        detailsContainer.addView(quantityInput)

        val unitsSpinner = Spinner(activity).apply {

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        detailsContainer.addView(unitsSpinner)

        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.units,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                unitsSpinner.adapter = adapter
            }
        }

        ingredientsContainer.addView(ingredientInput)
        ingredientsContainer.addView(detailsContainer)

        container.addView(ingredientsContainer)
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val mImageView = dialogView.findViewById<ImageView>(R.id.dish_photo_image_view)

        if (requestCode == PICK_IMAGE_REQUST && resultCode == RESULT_OK
            && data != null && data.data != null) {
            mImageUri = data.data!!

            Picasso
                .get()
                .load(mImageUri)
                .resize(1200, 600)
                .centerCrop()
                .into(mImageView)
        }
    }
}
