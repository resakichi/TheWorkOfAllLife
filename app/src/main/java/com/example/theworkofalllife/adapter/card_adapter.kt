package com.example.theworkofalllife

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cardview_items.view.*

class MyAdapter(val arrayList: ArrayList<Model>, val context: Context):
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindItems(model: Model){
            val image = BitmapFactory.decodeFile(model.image.toString())
            if (image != null && model.title != null){
                itemView.img_cart.setImageBitmap(image)
                itemView.cart_name.text = model.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_items, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
    }

    override fun getItemCount(): Int {
        return  arrayList.size
    }
}