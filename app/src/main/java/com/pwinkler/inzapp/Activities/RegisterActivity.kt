package com.pwinkler.inzapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pwinkler.inzapp.R

/**
 * Ekran panelu rejestracji, który pojawi się
 * po kliknięciu w przycisk "Zarejestruj się" na ekranie logowania
 */

class RegisterActivity: AppCompatActivity() {

    var fbAuth = FirebaseAuth.getInstance()
    var fbDatabase = FirebaseFirestore.getInstance()

    /**Podpięcie ekranu rejestracji**/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_panel)

        val emailTextView = findViewById<EditText>(R.id.input_email)
        val passwordTextView = findViewById<EditText>(R.id.input_password)

        /**Podpięcie przycisku, który wywołuje rejestrację**/
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Proszę wpisać adres e-mail i hasło", Toast.LENGTH_LONG).show()
            } else {
                createUser(email, password)
            }
        }
    }

    /**Funkcja tworząca użytkownika**/
    private fun createUser(email: String, password: String) {
        fbAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    initializeUserDatabase()
                } else {
                    Toast.makeText(this, "Wystąpił błąd: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    /**Funkcja inicjalizująca bazę
     * Zapisuje do bazy identyfikatory użytkownika
     */
    private fun initializeUserDatabase() {
        val uid = fbAuth.currentUser?.uid
        uid?.let {
            val user = HashMap<String, Any>()
            user["uid"] = fbAuth.currentUser?.uid ?: ""
            user["email"] = fbAuth.currentUser?.email ?: ""

            fbDatabase.collection("users")
                .document()
                .set(user)
                .addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Wystąpił błąd podczas inicjalizacji bazy", Toast.LENGTH_LONG).show()
                }
        }
    }
}