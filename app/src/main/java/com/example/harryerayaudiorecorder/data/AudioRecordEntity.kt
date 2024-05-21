package com.example.harryerayaudiorecorder.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "audioRecords")
data class AudioRecordEntity(
    var filename: String,
    var filePath: String,
    val duration: Int = 0,
    val fileSize: Double = 0.0,
    val date: String = ""
){
    @PrimaryKey(autoGenerate = true)
    var id = 0
    @Ignore
    var isChecked = false
}
