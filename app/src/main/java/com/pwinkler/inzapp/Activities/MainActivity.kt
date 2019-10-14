package com.pwinkler.inzapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.pwinkler.inzapp.R

class MainActivity : AppCompatActivity() {

    private var fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Jeżeli użytkownik się wylogował to pokazujemy ekran logowania
     * jeżeli użytkownik jest zalogowany to pokazujemy mu ekran wyboru
     */

    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser == null){
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
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
        finishAffinity()
        System.exit(0)
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar))
    }
}
