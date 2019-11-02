package com.pwinkler.inzapp.controllers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.R

class LoginActivity: AppCompatActivity() {

    var fbAuth = FirebaseAuth.getInstance()

    /**
     * Sprawdzamy czy użytkownik był już zalogowany
     * - jeśli tak to przechodzimy do ekranu głównego
     * - jeśli nie to przechodzimy do ekranu logowania
     */
    val authStateListener = FirebaseAuth.AuthStateListener{ firebaseAuth ->
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        fbAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        fbAuth.removeAuthStateListener(authStateListener)
    }

    override fun onBackPressed() {
        finishAffinity() //co to?
        System.exit(0)
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /**
         * Tworzymy wartości, których pola uzupełniane są na ekranie logowania
         */
        val emailTextView = findViewById<EditText>(R.id.input_email)
        val passwordTextView = findViewById<EditText>(R.id.input_password)

        /**
         * Podpinamy formularz logowania
         */
        val submitButton = findViewById<Button>(R.id.loginButton)
        submitButton.setOnClickListener {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()
            tryToLogIn(email, password)
        }

        /**
         * Podpinamy przycisk "Przypomnij hasło"
         */
        val forgotPasswordButton = findViewById<Button>(R.id.forgotPass)
        forgotPasswordButton.setOnClickListener {
            val username = emailTextView.text.toString()
            if (username.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity, "Proszę wpisać adres e-mail",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                fbAuth.sendPasswordResetEmail(username).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Wiadomość została wysłana na adres: $username",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        /**
         * Podpinamy przycisk "Nie masz konta? Zarejstruj się"
         */
        val registerButton = findViewById<Button>(R.id.noAccount)
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Funkcja, która odpowiada za przeniesienia użytkownika po poprawnym zalogowaniu
     * Jeżeli użytkownik wpisze błędne dane, funkcja zwróci stosowny komunikat
     */

    private fun tryToLogIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this@LoginActivity, "Proszę wpisać adres e-mail i hasło", Toast.LENGTH_LONG).show()
            return
        }

        fbAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener (this, OnCompleteListener<AuthResult>{
                task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this@LoginActivity, "Wystąpił błąd: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}