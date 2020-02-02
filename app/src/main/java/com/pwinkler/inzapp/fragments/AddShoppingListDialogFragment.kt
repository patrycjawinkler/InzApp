package com.pwinkler.inzapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.pwinkler.inzapp.R
import com.pwinkler.inzapp.helpers.DpSize
import java.lang.ClassCastException
import java.lang.IllegalStateException

class AddShoppingListDialogFragment : DialogFragment() {

    private lateinit var listener: ModalListener
    private lateinit var dialogView: View

    private var items = 1

    /**
     * Interfejs, który ShoppingListActivity musi zaimplementować,
     * aby mogło z niego korzystać
     */
    interface ModalListener {
        fun onDialogPositiveClick(
            name: String,
            items: ArrayList<String>
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
                "$context musi implemenować AddShoppingListDialogListener"
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity, R.style.FullScreenDialogTheme)

            val inflater = activity.layoutInflater
            dialogView = inflater.inflate(R.layout.add_shopping_list_modal, null)

            val container = dialogView.findViewById<LinearLayout>(R.id.list_items_container)

            for (i in 0 until container.childCount) {
                val itemsContainerHorizontal = container.getChildAt(i) as LinearLayout

                val deleteItemImageView = itemsContainerHorizontal.getChildAt(1) as ImageView

                deleteItemImageView.setOnClickListener {
                    if (container.childCount == 1) {
                        Toast.makeText(activity, "Nie można usunąć tego pola", Toast.LENGTH_LONG).show()
                    } else {
                        itemsContainerHorizontal.removeAllViews()
                    }
                }
            }

            /**
             * Podpięcie przycisku, który dodaje
             * kolejne pola tekstowe na liście zakupów
             */
            val addNewItem = dialogView.findViewById<ImageView>(R.id.add_item_button)

            addNewItem.setOnClickListener {
                addNewItemEditText()
            }

            addNewItem.setOnLongClickListener {
                Toast.makeText(activity, "Dodaj nową pozycję na liście", Toast.LENGTH_LONG).show()
                true
            }

            val dialog = builder.run {
                setView(dialogView)
                    .setPositiveButton("Utwórz", null)
                    .setNegativeButton("Anuluj", null)
                create()
            }

            dialog.apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {
                        val shoppingListNameEditText = dialogView.findViewById<EditText>(R.id.input_list_name)
                        val inputNameEditText = dialogView.findViewById<EditText>(R.id.input_item)
                        val itemContainer = dialogView.findViewById<LinearLayout>(R.id.list_items_container)

                        val itemList = arrayListOf<String>()
                        for (i in 0 until itemContainer.childCount) {
                            val itemLinearLayout = itemContainer.getChildAt(i) as LinearLayout
                            val itemName = (itemLinearLayout.getChildAt(0) as EditText).text.toString()
                            if (itemName.isNotBlank()) {
                                itemList.add(itemName)
                            } else {
                                Toast.makeText(activity, "Proszę uzupełnić brakujące pola", Toast.LENGTH_LONG).show()
                            }
                        }

                        if (shoppingListNameEditText.text.toString().isBlank() || inputNameEditText.text.toString().isBlank()) {
                            Toast.makeText(activity, "Proszę uzupełnić brakujące pola", Toast.LENGTH_LONG).show()
                        } else {
                            listener.onDialogPositiveClick(
                                shoppingListNameEditText.text.toString(),
                                itemList
                            )
                            dialog.dismiss()
                        }
                    }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun addNewItemEditText() {

        items++
        if (items > 30) {
            return
        }

        val container = dialogView.findViewById<LinearLayout>(R.id.list_items_container)

        val itemContainerHorizontal = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val itemInput = EditText(activity).apply {
            inputType = 16384
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                9.0f
            )
        }

        val deleteItem = ImageView(activity).apply {
            setImageResource(R.drawable.ic_remove_circle_primary_24dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        itemContainerHorizontal.addView(itemInput)
        itemContainerHorizontal.addView(deleteItem)

        container.addView(itemContainerHorizontal)
        Log.d("TAG", "container.childCount przed deleteItem = ${container.childCount}")

        deleteItem.setOnClickListener {
            if (container.childCount == 1) {
                Toast.makeText(activity, "Nie można usunąć tego pola", Toast.LENGTH_LONG).show()

            } else {
                itemContainerHorizontal.removeAllViews()
                container.removeView(itemContainerHorizontal)
                Log.d("TAG", "container.childCount po deleteItem = ${container.childCount}")
            }
        }
    }

}