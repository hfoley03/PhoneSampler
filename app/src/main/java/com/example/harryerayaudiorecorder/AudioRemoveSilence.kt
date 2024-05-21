package com.example.harryerayaudiorecorder

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File

object AudioRemoveSilence {

    fun trimSilenceFromAudio(inputFilePath: String, outputFilePath: String, onComplete: (Boolean) -> Unit) {
        // Build the FFmpeg command to trim silence

        val ffmpegCommand = "-i $inputFilePath -af silenceremove=1:0:-50dB -y $outputFilePath"

        // Execute the FFmpeg command
        FFmpegKit.executeAsync(ffmpegCommand) { session ->
            val returnCode = session.returnCode
            if (ReturnCode.isSuccess(returnCode)) {
                // Success
                onComplete(true)
                val inputFile = File(inputFilePath)
                if(inputFile.exists()){
                    inputFile.delete()
                    Log.d("AudioRemoveSilence", "deleted original file")
                }
            } else {
                // Failure
                onComplete(false)
            }
        }
    }

}