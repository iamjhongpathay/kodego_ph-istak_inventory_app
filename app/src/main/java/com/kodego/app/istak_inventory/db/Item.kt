package com.kodego.app.istak_inventory.db

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.ParcelField
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Item(
    var name: String,
    var description: String,
    var stock: Int,
    var imageItem: Bitmap
): Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
