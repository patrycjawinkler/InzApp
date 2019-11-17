package com.pwinkler.inzapp.helpers

import android.widget.Toast

class UploadImage {

    lateinit var mImageUrl: String

    fun Upload() {
        //wymagana pusta klasa
    }

    fun Upload(imageUrl: String) {
        mImageUrl = imageUrl
    }

    fun getImageUrl() : String {
        return mImageUrl
    }

    fun setImageUrl(imageUrl: String) {
        mImageUrl = imageUrl
    }
}