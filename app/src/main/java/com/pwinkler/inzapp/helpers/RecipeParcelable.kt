package com.pwinkler.inzapp.helpers

import android.os.Parcel
import android.os.Parcelable

/**
 * NIEDOKONCZONE!!!
 */

class RecipeParcelable() : Parcelable{

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeParcelable> {
        override fun createFromParcel(parcel: Parcel): RecipeParcelable {
            return RecipeParcelable(parcel)
        }

        override fun newArray(size: Int): Array<RecipeParcelable?> {
            return arrayOfNulls(size)
        }
    }


}