package com.pwinkler.inzapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.R
import com.pwinkler.inzapp.models.User
import java.lang.ClassCastException
import java.lang.NullPointerException

class SendRecipeDialogFragment : DialogFragment() {

    private lateinit var listener: ModalListener
    private lateinit var dialog: AlertDialog
    private val db = FirebaseFirestore.getInstance()

    /**
     *  Interfejs, który Activity musi zaimplementować jeśli chce korzystać z tego modala
     */
    interface ModalListener {
        fun onSendRecipePositiveClick(
            user: User
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
            val dialogView = inflater.inflate(R.layout.send_recipe_modal, null)

            dialog = builder.run {
                setView(dialogView)
                    .setPositiveButton("Wyślij", null)
                    .setNegativeButton("Anuluj", null)
                create()
            }

            dialog.apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {
                        val emailEditText = dialogView.findViewById<EditText>(R.id.enter_email_edit_text)
                        proceedIfUserExist(emailEditText.text.toString())
                    }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun proceedIfUserExist(email: String) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Toast.makeText(context, "Podany użytkownik nie istnieje", Toast.LENGTH_LONG).show()
                } else {
                    val doc = it.documents[0]
                    val data = doc.data ?: throw NullPointerException()

                    val user = User(
                        doc.id,
                        (data["email"] ?: throw NoSuchFieldException()) as String,
                        (data["name"] ?: throw NoSuchFieldException()) as String,
                        (data["invites"] ?: throw NoSuchFieldException()) as ArrayList<String>
                    )

                    listener.onSendRecipePositiveClick(user)
                    dialog.dismiss()
                }

            }

    }
}