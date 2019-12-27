package com.pwinkler.inzapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pwinkler.inzapp.models.Product

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()

    private val collectionPath = "/products"


    /**
     * Funkcja, która dodaje produkt do kolekcji
     */

    fun addProduct(name: String, quantity: String, unit: String) {

        //Utworz nowy dokument i pobierz referencje (id jest tworzone automatycznie)
        val productReference = db.collection(collectionPath).document()

        val updatedProduct = Product(productReference.id, name, quantity, unit)

        productReference.set(
            HashMap<String, Any>().apply {
                this["id"] = updatedProduct.id
                this["name"] = updatedProduct.name
                this["quantity"] = updatedProduct.quantity
                this["unit"] = updatedProduct.unit
            },
            SetOptions.merge()
        )
    }
}