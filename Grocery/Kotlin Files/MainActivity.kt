package com.rahulpa.groceryapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(),GroceryRVAdapter.GroceryItemClickInterface {
    lateinit var itemsRV: RecyclerView
    lateinit var addFAB: FloatingActionButton
    lateinit var list: List<GroceryItems>
    lateinit var groceryRVAdapter: GroceryRVAdapter
    lateinit var grocerViewModal:GroceryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        itemsRV=findViewById(R.id.idRVItems)
        addFAB=findViewById(R.id.idFABAdd)
        list=ArrayList<GroceryItems>()
        groceryRVAdapter = GroceryRVAdapter(list,this)
        itemsRV.layoutManager=LinearLayoutManager(this)
        itemsRV.adapter = groceryRVAdapter
        val groceryRepository = GroceryRepository(GroceryDatabase(this))
        val factory = GroceryViewModelFactory(groceryRepository)
        grocerViewModal = ViewModelProvider(this,factory).get(GroceryViewModel::class.java)
        grocerViewModal.allGroceryItems().observe(this, Observer {
            groceryRVAdapter.list=it
            groceryRVAdapter.notifyDataSetChanged()
        })
        addFAB.setOnClickListener{
            openDialog()
        }

    }

    fun openDialog(){
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.grocery_dialog)
        val cancelBtn = dialog.findViewById<Button>(R.id.idCancelButton)
        val addBtn = dialog.findViewById<Button>(R.id.idAddButton)
        val itemEdt = dialog.findViewById<EditText>(R.id.idEditItemName)
        val itemPriceEdt = dialog.findViewById<EditText>(R.id.idEditItemPrice)
        val itemQtyEdt = dialog.findViewById<EditText>(R.id.idEditItemQty)
        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }
        addBtn.setOnClickListener {
            val itemName:String = itemEdt.text.toString()
            val itemPrice:String = itemPriceEdt.text.toString()
            val itemQty:String = itemQtyEdt.text.toString()
            val qty:Int = itemQty.toInt()
            val pr : Int = itemPrice.toInt()
            if(itemName.isNotEmpty()&&itemPrice.isNotEmpty()&&itemQty.isNotEmpty()){
                val items = GroceryItems(itemName,qty,pr)
                grocerViewModal.insert(items)
                Toast.makeText(applicationContext,"Item Inserted...",Toast.LENGTH_SHORT).show()
                groceryRVAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }else{
                Toast.makeText(applicationContext,"Enter all data",Toast.LENGTH_SHORT).show()
            }

        }
        dialog.show()
    }

    override  fun onItemClick(groceryItems: GroceryItems){
        grocerViewModal.delete(groceryItems)
        groceryRVAdapter.notifyDataSetChanged()
        Toast.makeText(applicationContext,"Item Deleated...",Toast.LENGTH_SHORT).show()
    }

}
