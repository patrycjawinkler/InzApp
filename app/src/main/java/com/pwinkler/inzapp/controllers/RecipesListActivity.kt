package com.pwinkler.inzapp.controllers

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.pwinkler.inzapp.R

class RecipesListActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.my_recipes_list)
        setSupportActionBar(findViewById(R.id.my_recipes_toolbar))
    }
}