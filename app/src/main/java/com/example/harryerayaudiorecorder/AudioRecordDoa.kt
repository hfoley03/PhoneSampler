package com.example.harryerayaudiorecorder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.harryerayaudiorecorder.data.AudioRecordEntity

@Dao
interface AudioRecordDoa {
    @Query("SELECT * FROM audioRecords")
    fun getAll(): List<AudioRecordEntity>

    @Insert
    fun insert(vararg audioRecordEntity: AudioRecordEntity)

    @Delete
    fun delete(audioRecordEntity: AudioRecordEntity)

    @Delete
    fun delete(audioRecordEntity: Array<AudioRecordEntity>)

    @Update
    fun update(audioRecordEntity: AudioRecordEntity)

    @Query("DELETE FROM audioRecords")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM audioRecords WHERE filename = :fileName)")
    suspend fun doesAudioRecordExist(fileName: String): Boolean
}