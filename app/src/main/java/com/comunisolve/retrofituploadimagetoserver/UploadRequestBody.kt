package com.comunisolve.retrofituploadimagetoserver

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadRequestBody(
    private val file: File,
    private val contentType: String,
    private val callback: UploadCallback
) : RequestBody() {


    interface UploadCallback {
        fun onProgressUpdate(percentage: Int)
    }

    inner class ProgressUpdate(
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {

        override fun run() {
            callback.onProgressUpdate((100 * uploaded / total).toInt())
        }

    }

    override fun contentType() = MediaType.parse("$contentType/*")

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val lenght = file.length()
        val buffer = ByteArray(DEFAUL_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        // use method is used when we use an android resource and don;t need to be worry bout closing it.
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())

            while (inputStream.read(buffer).also {
                    read = it
                } != -1) { // we read all data till last, -1 means end position
                handler.post(ProgressUpdate(uploaded, lenght))

                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }


    companion object {
        private const val DEFAUL_BUFFER_SIZE = 1048
    }
}