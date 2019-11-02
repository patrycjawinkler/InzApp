package com.pwinkler.inzapp.helpers

import android.content.Context

/**
 * Klasa używana do pomocy w obliczeniu rozmiaru dp, wzgledem gęstości ekranu
 * */
class DpSize(val context: Context){

    fun dpToPx(dp: Int): Int{
        val density = context.resources
            .displayMetrics
            .density
        return Math.round(dp.toFloat() * density)
    }
}