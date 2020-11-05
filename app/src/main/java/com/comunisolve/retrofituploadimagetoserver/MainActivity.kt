package com.comunisolve.retrofituploadimagetoserver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    private var selectedImage: Uri? = null

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectImage.setOnClickListener {
            openImageChooser()
        }

        upload_btn.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {

        if (selectedImage == null) {
            layout_root.snackBar("Select an Image First")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openAssetFileDescriptor(selectedImage!!, "r", null) ?: return
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        progress_bar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        MyAPI().uploadImage(
            MultipartBody.Part.createFormData("image",file.name,body),
            RequestBody.create(MediaType.parse("multipart/form-data"),"Image From My Device")
        ).enqueue(object :Callback<UploadResponse>{
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                progress_bar.progress=100
                layout_root.snackBar(response.body()!!.message.toString())
                Picasso.get().load(response!!.body()!!.image).into(selectImage)
                Toast.makeText(this@MainActivity,""+response!!.body()!!.image,Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                layout_root.snackBar("[Error]"+t.message)
            }

        })
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimetype = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimetype)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImage = data!!.data
                    selectImage.setImageURI(selectedImage)
                }
            }
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        progress_bar.progress=percentage

    }
}