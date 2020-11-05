package com.comunisolve.retrofituploadimagetoserver

data class UploadResponse (
    val error:Boolean,
    val message:String,
    val image:String?
)