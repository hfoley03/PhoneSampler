package com.example.harryerayaudiorecorder.data

// Import necessary packages and libraries
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
    fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>)
    fun renameSoundCard(soundCard: SoundCard, newFileName: String, soundCardList: SnapshotStateList<MutableState<SoundCard>>)
    fun trimAudio(file: File, startMillis: Int, endMillis: Int, onTrimmed: (File) -> Unit)
    suspend fun getAllAudioRecords(): List<AudioRecordEntity>
    fun saveTrimmed(file: File)
    fun renameFile(newName: String)
    fun renameFileFromList(oldName: String, newName: String)
    fun getAudioDuration(file: File) : Int

    fun getLastCreatedFile(directory: File): File?
}

class MyAudioRepository(
    private val db: AudioRecordDatabase,
    val audioCapturesDirectory: File
) : AudioRepository {
    // Save audio file details to the database
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


    // Delete a sound card and its associated audio file from the database and filesystem
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

    // Rename a sound card in the database and update the UI
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

    // Trim an audio file and save the result
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

    // Get the last created file in a directory
    override fun getLastCreatedFile(directory: File): File? {
        return directory.listFiles()?.sortedByDescending { it.lastModified() }?.firstOrNull()
    }

    // Rename an audio file to a new name
    override fun renameFile(newName: String) {
        val file = getLastCreatedFile(audioCapturesDirectory)

        if (file != null) {
            if (file.exists()) {
                val newFile = File(audioCapturesDirectory, newName)
                if (!newFile.exists()) {
                    file.renameTo(newFile)
                } else {
                    // Handle the case where a file with the new name already exists
                }
            } else {
                Log.d("AudioRepository", "no such file")
            }
        }
    }

    // Rename a file from a list of files
    override fun renameFileFromList(oldName: String, newName: String) {
        val file = File(audioCapturesDirectory, oldName)
        if (file.exists()) {
            val newFile = File(audioCapturesDirectory, newName)
            if (!newFile.exists()) {
                file.renameTo(newFile)
            } else {
                // Handle the case where a file with the new name already exists
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
        // Mock implementation
        val file = File(name)
        val dur = getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        val record = AudioRecordEntity(file.name, file.absolutePath, dur, fSizeMB, lastModDate)
        mockData.add(record)
    }

    override fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        // Mock implementation
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
        // Mock implementation
        val record = mockData.find { it.filename == soundCard.fileName }
        record?.filename = newFileName
        val index = soundCardList.indexOfFirst { it.value.fileName == soundCard.fileName }
        if (index != -1) {
            soundCardList[index].value = soundCard.copy(fileName = newFileName)
        }
    }

    override fun trimAudio(file: File, startMillis: Int, endMillis: Int, onTrimmed: (File) -> Unit) {
        // Mock implementation
        val outputTrimmedFile = File("trimmed_${file.name}")
        onTrimmed(outputTrimmedFile)
        saveTrimmed(outputTrimmedFile)
    }

    override suspend fun getAllAudioRecords(): List<AudioRecordEntity> {
        return mockData
    }

    override fun saveTrimmed(file: File) {
        // Mock implementation
        val dur = getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        val name = file.name
        val record = AudioRecordEntity(name, file.absolutePath, dur, fSizeMB, lastModDate)
        mockData.add(record)
    }

    override fun getLastCreatedFile(directory: File): File? {
        // Mock implementation
        return directory.listFiles()?.sortedByDescending { it.lastModified() }?.firstOrNull()
    }

    override fun renameFile(newName: String) {
        // Mock implementation
        val file = getLastCreatedFile(File("."))
        file?.renameTo(File(newName))
    }

    override fun renameFileFromList(oldName: String, newName: String) {
        // Mock implementation
        val file = File(oldName)
        file.renameTo(File(newName))
    }

    override fun getAudioDuration(file: File): Int {
        // Mock implementation
        return 100 // return a mock duration value
    }
}
