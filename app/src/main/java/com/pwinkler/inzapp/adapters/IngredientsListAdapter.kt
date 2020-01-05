package com.pwinkler.inzapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.pwinkler.inzapp.models.Product

class IngredientsListAdapter(context: Context, var ingredients: ArrayList<Product>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val productList = emptyList<Product>()

    override fun getCount(): Int {
        return productList.count()
    }

    override fun getItemId(p0: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItem(p0: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class ViewHolder(view: View?) {



    }

}