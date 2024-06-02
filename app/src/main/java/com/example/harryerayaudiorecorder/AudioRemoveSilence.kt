package com.example.harryerayaudiorecorder

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File

object AudioRemoveSilence {

    fun trimSilenceFromAudio(inputFilePath: String, outputFilePath: String, onComplete: (Boolean) -> Unit) {

        // FFMpeg command to create a copy of the input file with the silence removed from the start and end
        val ffmpegCommand = "-i $inputFilePath -af silenceremove=1:0:-50dB -y $outputFilePath"

        FFmpegKit.executeAsync(ffmpegCommand) { session ->
            val returnCode = session.returnCode
            if (ReturnCode.isSuccess(returnCode)) {
                onComplete(true)
                val inputFile = File(inputFilePath)
                if(inputFile.exists()){
                    inputFile.delete() // need to then delete the original file
                    Log.d("AudioRemoveSilence", "deleted original file")
                }
            } else {
                onComplete(false)
            }
        }
    }

}