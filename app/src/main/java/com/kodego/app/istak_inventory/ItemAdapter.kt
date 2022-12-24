package com.kodego.app.istak_inventory

import android.annotation.SuppressLint
import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.kodego.app.istak_inventory.databinding.RowItemsBinding
import com.kodego.app.istak_inventory.db.Item
import com.kodego.app.istak_inventory.fragments.list.ItemListFragmentDirections
import kotlinx.coroutines.withContext

class ItemAdapter(var itemModel: MutableList<Item>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    var onItemDelete : ((Item, Int) -> Unit) ? = null

    inner class ItemViewHolder(var binding: RowItemsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowItemsBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.apply {
            tvItemName.text = itemModel[position].name
            tvItemStock.text = itemModel[position].stock.toString()
            ivRowItemPhoto.setImageBitmap(itemModel[position].imageItem)

        }
        //showed up the view item fragment when clicked the row item
        holder.itemView.setOnClickListener(){
            val action = ItemListFragmentDirections.actionItemListFragmentToViewItemFragment(itemModel[position])
            holder.itemView.findNavController().navigate(action)
        }

        //Option Menu
        holder.binding.btnOptionMenu.setOnClickListener(){
            val popupMenu = PopupMenu(holder.itemView.context, holder.binding.btnOptionMenu)
            popupMenu.inflate(R.menu.options_menu)

            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when(item?.itemId){
                        R.id.edit -> {
                            val action = ItemListFragmentDirections.actionItemListFragmentToUpdateItemFragment(itemModel[position])
                            holder.itemView.findNavController().navigate(action)
                            return true
                        }
                        R.id.delete -> {
                            onItemDelete?.invoke(itemModel[position], position)
                            return true
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return itemModel.size
    }

    //refresh the recyclerview if any changes
    fun setData(item: MutableList<Item>){
        this.itemModel = item
        notifyDataSetChanged()
    }
}