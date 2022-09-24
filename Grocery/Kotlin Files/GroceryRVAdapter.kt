package com.rahulpa.groceryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroceryRVAdapter(
    var list: List<GroceryItems>,
    var groceryItemClickInterface: GroceryItemClickInterface
): RecyclerView.Adapter<GroceryRVAdapter.GroceryViewHolder>() {

    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTV = itemView.findViewById<TextView>(R.id.idTVItemName)
        val quantityTV = itemView.findViewById<TextView>(R.id.idTVQuantity)
        val rateTV = itemView.findViewById<TextView>(R.id.idTVRate)
        val amountTV = itemView.findViewById<TextView>(R.id.idTVTotalAmount)
        val deleteTV = itemView.findViewById<ImageView>(R.id.idTVDelete)

    }

    interface GroceryItemClickInterface{
        fun onItemClick(groceryItems: GroceryItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grocery_rv_item, parent, false)
        return GroceryViewHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.nameTV.text= list[position].itemName
        holder.rateTV.text = list[position].itemPrice.toString()
        holder.quantityTV.text = "Rs. "+list[position].itemQuantity.toString()
        var itemTotal:Int = list[position].itemPrice*list[position].itemQuantity
        holder.amountTV.text="Rs."+itemTotal.toString()
        holder.deleteTV.setOnClickListener{
            groceryItemClickInterface.onItemClick(list[position])
        }

    }

}
