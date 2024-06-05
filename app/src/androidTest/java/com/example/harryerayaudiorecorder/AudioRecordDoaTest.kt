//package com.example.harryerayaudiorecorder
//
//import android.content.Context
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.harryerayaudiorecorder.data.AudioRecordDatabase
//import com.example.harryerayaudiorecorder.data.AudioRecordEntity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.withContext
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertNotNull
//import org.junit.Assert.assertTrue
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.annotation.Config
//
//@Config(manifest = Config.NONE)
//@RunWith(AndroidJUnit4::class)
//class AudioRecordDoaTest {
//
//    private lateinit var db: AudioRecordDatabase
//    private lateinit var dao: AudioRecordDoa
//
//    @Before
//    fun createDb() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//            context, AudioRecordDatabase::class.java).build()
//        dao = db.audioRecordDoa()
//    }
//
//    @After
//    fun closeDb() {
//        db.close()
//    }
//
//    @Test
//    fun testInsertAndRetrieveAudioRecord() = runBlocking {
//        val record = AudioRecordEntity(
//            filename = "testFile.wav",
//            filePath = "/path/to/testFile.wav",
//            duration = 1000,
//            fileSize = 1.2
//        )
//
//        withContext(Dispatchers.IO) {
//            dao.insert(record)
//        }
//
//        val retrievedRecords = withContext(Dispatchers.IO) {
//            dao.getAll()
//        }
//
//        assertEquals(1, retrievedRecords.size)
//        assertEquals(record.filename, retrievedRecords[0].filename)
//    }
//
//    @Test
//    fun testDeleteAudioRecord() = runBlocking {
//        val record = AudioRecordEntity(
//            filename = "testFile.wav",
//            filePath = "/path/to/testFile.wav",
//            duration = 1000,
//            fileSize = 1.2
//        )
//
//        withContext(Dispatchers.IO) {
//            dao.insert(record)
//        }
//
//        val retrievedRecord = withContext(Dispatchers.IO) {
//            dao.getAll().firstOrNull()
//        }
//
//        assertNotNull(retrievedRecord)
//
//        withContext(Dispatchers.IO) {
//            dao.delete(retrievedRecord!!)
//        }
//
//        val remainingRecords = withContext(Dispatchers.IO) {
//            dao.getAll()
//        }
//
//        assertTrue(remainingRecords.isEmpty())
//    }
//
////    @Test
////    fun testUpdateAudioRecord() = runBlocking {
////        val record = AudioRecordEntity(
////            filename = "testFile.wav",
////            filePath = "/path/to/testFile.wav",
////            duration = 1000,
////            fileSize = 1.2
////        )
////
////        withContext(Dispatchers.IO) {
////            dao.insert(record)
////        }
////
////        var retrievedRecord = withContext(Dispatchers.IO) {
////            dao.getAll().firstOrNull()
////        }
////
////        assertNotNull(retrievedRecord)
////        retrievedRecord?.let {
////            val updatedRecord = it.copy(filename = "updatedTestFile.wav")
////
////            withContext(Dispatchers.IO) {
////                dao.update(updatedRecord)
////            }
////
////            val finalRecord = withContext(Dispatchers.IO) {
////                dao.getAll().firstOrNull()
////            }
////
////            assertNotNull(finalRecord)
////            assertEquals("updatedTestFile.wav", finalRecord?.filename)
////        }
////    }
//}
