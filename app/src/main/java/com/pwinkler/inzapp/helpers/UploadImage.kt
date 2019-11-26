package com.pwinkler.inzapp.helpers

import android.widget.Toast

class UploadImage {

    lateinit var mName: String
    lateinit var mImageUrl: String

    fun Upload() {
        //wymagany pusty konstruktor
    }

    fun Upload(name: String, imageUrl: String) {
        mName = name
        mImageUrl = imageUrl
    }

    fun getName() : String {
        return mName
    }

    fun setName(name: String) {
        mName = name
    }

    fun getImageUrl() : String {
        return mImageUrl
    }

    fun setImageUrl(imageUrl: String) {
        mImageUrl = imageUrl
    }
}