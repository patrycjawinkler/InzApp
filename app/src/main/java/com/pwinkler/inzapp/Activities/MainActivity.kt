package com.pwinkler.inzapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pwinkler.inzapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar))
    }
}
