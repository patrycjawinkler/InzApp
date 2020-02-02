package com.pwinkler.inzapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.R
import com.pwinkler.inzapp.models.Recipe
import java.lang.ClassCastException

class DeleteRecipeDialogFragment : DialogFragment(){

    private lateinit var listener: ModalListener
    private lateinit var dialog: AlertDialog
    private val db = FirebaseFirestore.getInstance()

    /**
     *  Interfejs, który Activity musi zaimplementować jeśli chce korzystać z tego modala
     */
    interface ModalListener {
        fun onDeleteRecipePositiveClick(
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as ModalListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        "must implement SendRecipeDialogFragment")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->

            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            val dialogView = inflater.inflate(R.layout.delete_recipe_modal, null)

            dialog = builder.run {
                setView(dialogView)
                    .setPositiveButton("Usuń", null)
                    .setNegativeButton("Anuluj", null)
                create()
            }

            dialog.apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {
                        listener.onDeleteRecipePositiveClick()
                        dialog.dismiss()
                    }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}