package com.comunisolve.retrofituploadimagetoserver


import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackBar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_SHORT
    )
        .also { snackbar ->
            snackbar.setAction("Ok") {
                snackbar.dismiss()
            }
        }.show()


}

fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val cursor =
            query(uri, null, null, null)
        cursor.use {
            it!!.moveToFirst()
            name = cursor!!.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }
    return name!!
}