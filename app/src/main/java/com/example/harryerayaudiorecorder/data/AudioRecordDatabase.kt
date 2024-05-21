package com.example.harryerayaudiorecorder.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.harryerayaudiorecorder.AudioRecordDoa

@Database(entities = arrayOf(AudioRecordEntity::class), version = 2)
abstract class AudioRecordDatabase : RoomDatabase() {
    abstract  fun audioRecordDoa() : AudioRecordDoa

}