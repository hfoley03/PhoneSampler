package com.example.harryerayaudiorecorder.data

import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arthenica.ffmpegkit.FFmpegKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


interface AudioRepository {
    fun save(name: String)
    fun saveFromFile(file:File)
    fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>)
    fun renameSoundCard(soundCard: SoundCard, newFileName: String, soundCardList: SnapshotStateList<MutableState<SoundCard>>)
    fun trimAudio(file: File, startMillis: Int, endMillis: Int, onTrimmed: (File) -> Unit)
    suspend fun getAllAudioRecords(): List<AudioRecordEntity>
    fun saveTrimmed(file: File)
    fun renameFile(newName: String)
    fun renameFileFromList(oldName: String, newName: String)
    fun getAudioDuration(file: File) : Int

    fun getLastCreatedFile(directory: File): File?

    fun deleteAllRecords()

    suspend fun syncFolderandDatabase()
}

open class MyAudioRepository(
    private val db: AudioRecordDatabase,
    val audioCapturesDirectory: File
) : AudioRepository {
    override fun save(name: String) {
        //val file = File(audioCapturesDirectory, name)
        val file = getLastCreatedFile(audioCapturesDirectory)

        if(file != null){
            Log.d("saving", file.absolutePath);
            Log.d("saving", file.name);
            val dur = getAudioDuration(file)
            val fSizeMB = file.length().toDouble() / (1024 * 1024)
            val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date(file.lastModified()))

            val record = AudioRecordEntity(file.name, file.absolutePath, dur, fSizeMB, lastModDate)

            GlobalScope.launch {
                db.audioRecordDoa().insert(record)
            }
        }
    }


    override fun saveFromFile(file:File) {
        //val file = File(audioCapturesDirectory, name)

        if(file != null){
            Log.d("saving", file.absolutePath);
            Log.d("saving", file.name);
            val dur = getAudioDuration(file)
            val fSizeMB = file.length().toDouble() / (1024 * 1024)
            val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date(file.lastModified()))

            val record = AudioRecordEntity(file.name, file.absolutePath, dur, fSizeMB, lastModDate)

            GlobalScope.launch {
                db.audioRecordDoa().insert(record)
            }
        }
    }


    override fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        GlobalScope.launch(Dispatchers.IO) {
            val audioRecordEntity = db.audioRecordDoa().getAll()
                .find { it.filename == soundCard.fileName } // Assuming filename is unique
            audioRecordEntity?.let {
                db.audioRecordDoa().delete(it)

                val file = File(it.filePath)
                if (file.exists()) {
                    file.delete()
                    Log.d("DELETE", "DELETED")
                }

                withContext(Dispatchers.Main) {
                    soundCardList.removeIf { it.value.fileName == soundCard.fileName }
                }
            }
        }
    }

    override fun renameSoundCard(soundCard: SoundCard, newFileName: String, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        GlobalScope.launch(Dispatchers.IO) {
            val audioRecordEntity = db.audioRecordDoa().getAll()
                .find { it.filename == soundCard.fileName } // Assuming filename is unique
            audioRecordEntity?.let { entity ->
                entity.filename = newFileName
                db.audioRecordDoa().update(entity)

                withContext(Dispatchers.Main) {
                    val index = soundCardList.indexOfFirst { it.value.fileName == soundCard.fileName }
                    if (index != -1) {
                        soundCardList[index].value = soundCard.copy(fileName = newFileName)
                    }
                }
            }
        }
    }

    override fun trimAudio(file: File, startMillis: Int, endMillis: Int, onTrimmed: (File) -> Unit) {
        val outputTrimmedFile = File(audioCapturesDirectory, "trimmed_${file.name}")
        if (outputTrimmedFile.exists()) {
            outputTrimmedFile.delete()
        }
        val startSeconds = startMillis / 1000
        val durationSeconds = (endMillis - startMillis) / 1000
        val command = "-i ${file.absolutePath} -ss $startSeconds -t $durationSeconds -c copy ${outputTrimmedFile.absolutePath}"

        GlobalScope.launch(Dispatchers.IO) {
            FFmpegKit.execute(command).apply {
                if (returnCode.isSuccess) {
                    Log.d("AudioRepository", "Trimming successful: ${outputTrimmedFile.absolutePath}")
                    withContext(Dispatchers.Main) {
                        onTrimmed(outputTrimmedFile)
                        saveTrimmed(outputTrimmedFile)
                    }
                } else {
                    Log.e("AudioRepository", "Trimming failed")
                }
            }
        }
    }

    override fun saveTrimmed(file: File){
        val dur = getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date(file.lastModified()))
        val name = file.name
        val record = AudioRecordEntity(name, file.absolutePath, dur, fSizeMB, lastModDate)

        GlobalScope.launch {
            db.audioRecordDoa().insert(record)
        }
    }

    override fun getLastCreatedFile(directory: File): File? {
        return directory.listFiles()?.sortedByDescending { it.lastModified() }?.firstOrNull()
    }

    override fun deleteAllRecords() {
    }

    override suspend fun syncFolderandDatabase() {

        GlobalScope.launch {
            val dbEntries = db.audioRecordDoa().getAll()
            val dbFilePaths = dbEntries.map {it.filePath}.toSet()


            val filesiInFolder = audioCapturesDirectory.listFiles()

            if (filesiInFolder == null || filesiInFolder.isEmpty()) {
                Log.d("AudioRep", "No files found in directory: ${audioCapturesDirectory.absolutePath}")

            } else {
                Log.d("AudioRep", audioCapturesDirectory.listFiles().toString())

            }

            Log.d("AudioRepo", "syncing files")

            filesiInFolder?.forEach { file ->
                Log.d("AudioRecordRepository", "Found file: ${file.absolutePath}")
                if (file.isFile && !dbFilePaths.contains(file.absolutePath)){
                    Log.d("AudioRepo", "found file not in db")
//                    val dur = getAudioDuration(file)
//                    val fSizeMB = file.length().toDouble() / (1024 * 1024)
//                    val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date(file.lastModified()))
//                    val record = AudioRecordEntity(file.name, file.absolutePath, dur, fSizeMB, lastModDate)
//                    db.audioRecordDoa().insert(record)
                }
            }
        }
    }

    override fun renameFile(newName: String) {
        val file = getLastCreatedFile(audioCapturesDirectory)

        if (file != null) {
            if (file.exists()) {
                val newFile = File(audioCapturesDirectory, newName)
                if (!newFile.exists()) {
                    file.renameTo(newFile)
                } else {
                }
            } else {
                Log.d("AudioRepository", "no such file")
            }
        }
    }

    override fun renameFileFromList(oldName: String, newName: String) {
        val file = File(audioCapturesDirectory, oldName)
        if (file.exists()) {
            val newFile = File(audioCapturesDirectory, newName)
            if (!newFile.exists()) {
                file.renameTo(newFile)
            } else {
            }
        } else {
            Log.d("AudioRepository", "no such file")
        }
    }

    override suspend fun getAllAudioRecords(): List<AudioRecordEntity> {
        return db.audioRecordDoa().getAll()
    }

    override fun getAudioDuration(file: File): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return durationStr?.toIntOrNull() ?: 0
    }
}

class MockAudioRepository : AudioRepository {
    private val mockData = mutableListOf<AudioRecordEntity>()

    override fun save(name: String) {
        val file = File(name)
        val dur = getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        val record = AudioRecordEntity(file.name, file.absolutePath, dur, fSizeMB, lastModDate)
        mockData.add(record)
    }

    override fun saveFromFile(file: File) {
        // Mock implementation
        val dur = getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        val record = AudioRecordEntity(file.name, file.absolutePath, dur, fSizeMB, lastModDate)
        mockData.add(record)
    }

    override fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        val iterator = mockData.iterator()
        while (iterator.hasNext()) {
            val record = iterator.next()
            if (record.filename == soundCard.fileName) {
                iterator.remove()
                break
            }
        }
        soundCardList.removeIf { it.value.fileName == soundCard.fileName }
    }

    override fun renameSoundCard(soundCard: SoundCard, newFileName: String, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        val record = mockData.find { it.filename == soundCard.fileName }
        record?.filename = newFileName
        val index = soundCardList.indexOfFirst { it.value.fileName == soundCard.fileName }
        if (index != -1) {
            soundCardList[index].value = soundCard.copy(fileName = newFileName)
        }
    }

    override fun trimAudio(file: File, startMillis: Int, endMillis: Int, onTrimmed: (File) -> Unit) {
        val outputTrimmedFile = File("trimmed_${file.name}")
        onTrimmed(outputTrimmedFile)
        println(outputTrimmedFile.name)
        println("outputfilename")
        saveTrimmed(outputTrimmedFile)
    }



    override fun saveTrimmed(file: File) {
        val dur = getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        val name = file.name
        val record = AudioRecordEntity(name, file.absolutePath, dur, fSizeMB, lastModDate)
        mockData.add(record)
    }

    override suspend fun getAllAudioRecords(): List<AudioRecordEntity> {
        return mockData
    }
    override fun getLastCreatedFile(directory: File): File? {
        return directory.listFiles()?.sortedByDescending { it.lastModified() }?.firstOrNull()
    }

    override fun deleteAllRecords() {
        TODO("Not yet implemented")
    }

    override suspend fun syncFolderandDatabase() {
        val file = File("/storage/emulated/0/Music/testSong/trimmed_tomatoes.wav")
        val dur = getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        Log.d("testfile", file.name)
        Log.d("testfile", file.absolutePath)
        Log.d("testfile", dur.toString())
        Log.d("testfile",fSizeMB.toString())
        Log.d("testfile", lastModDate)


        val record = AudioRecordEntity(file.name, file.absolutePath, dur, fSizeMB, lastModDate)
        mockData.add(record)
    }

    override fun renameFile(newName: String) {
        val file = getLastCreatedFile(File("."))
        file?.renameTo(File(newName))
    }

    override fun renameFileFromList(oldName: String, newName: String) {
        val file = File(oldName)
        file.renameTo(File(newName))
    }

    override fun getAudioDuration(file: File): Int {
        return 20
    }



}
