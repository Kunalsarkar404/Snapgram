package com.example.snapgram.Utils

import android.app.ProgressDialog
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callback: (String?) -> Unit) {
    var imageUrl: String? = null
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                callback(imageUrl)
            }
        }
}

fun uploadVideo(uri: Uri, folderName: String, progressDialog: ProgressDialog, callback: (String?) -> Unit) {
    var reelUrl: String? = null
    progressDialog.setTitle("Uploading...")
    progressDialog.show()
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                reelUrl = it.toString()
                progressDialog.dismiss()
                callback(reelUrl)
            }
        }
        .addOnProgressListener {
            val uploadedValue: Int = ((it.bytesTransferred.toDouble() / it.totalByteCount.toDouble()) * 100).toInt()
            progressDialog.setMessage("Uploaded $uploadedValue %")
        }


}