package com.kodego.app.istak_inventory.db

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert
    fun addItem(item: Item)

    @Query("SELECT * FROM Item")
    fun getAllItems(): MutableList<Item>

    @Query("UPDATE Item SET name = :name, description = :description, stock = :stock, imageItem = :imageItem WHERE id = :id")
    fun updateItem(id: Int, name: String, description: String, stock: Int, imageItem: Bitmap)

    @Query("DELETE FROM Item WHERE id = :id")
    fun deleteItem(id: Int)

    @Query("SELECT * FROM Item WHERE name LIKE :searchQuery")
    fun searchItemName(searchQuery: String): MutableList<Item>
}