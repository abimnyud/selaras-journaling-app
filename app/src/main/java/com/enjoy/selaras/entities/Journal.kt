package com.enjoy.selaras.entities

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity("journals")
class Journal : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo("title")
    var title: String? = null

    @ColumnInfo("content")
    var content: String? = null

    @ColumnInfo("picture_url")
    var pictureUrl: String? = null

    @ColumnInfo("color")
    var color: String? = null

    @ColumnInfo(name = "emotion", )
    var emotion: String? = null

    @ColumnInfo("datetime")
    var dateTime: String? = null

    @Ignore var selected: Boolean = false

    override fun toString(): String {
        return "$title : $dateTime"
    }
}
