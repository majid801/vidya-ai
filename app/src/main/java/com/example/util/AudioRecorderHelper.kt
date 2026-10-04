package com.example.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.FileInputStream

class AudioRecorderHelper(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    var isRecording: Boolean = false
        private set

    fun startRecording(): Boolean {
        return try {
            val audioFile = File.createTempFile("student_voice_", ".m4a", context.cacheDir)
            currentOutputFile = audioFile

            val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            newRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(64000)
                setAudioSamplingRate(16000)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }

            recorder = newRecorder
            isRecording = true
            true
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to start recording", e)
            isRecording = false
            false
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) return null
        return try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            isRecording = false

            val file = currentOutputFile
            if (file != null && file.exists() && file.length() > 0) {
                val bytes = ByteArray(file.length().toInt())
                FileInputStream(file).use { it.read(bytes) }
                file.delete()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to stop recording", e)
            isRecording = false
            null
        }
    }
}
