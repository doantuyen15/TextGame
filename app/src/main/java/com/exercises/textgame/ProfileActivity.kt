package com.exercises.textgame

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

class ProfileActivity : BaseActivity() {
    private val REQUEST_CODE = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val data = intent.extras
        tvPlayerNameProf.text =
            fireBaseAuthInstance.currentUser?.displayName ?: data?.getString(USER_USERNAME_KEY)
        tvEmailProf.text =
            fireBaseAuthInstance.currentUser?.email ?: data?.getString(USER_EMAIL_KEY)
        Log.d("ProfileActivity****", fireBaseAuthInstance.currentUser?.photoUrl.toString())
        Glide.with(this)
            .load(fireBaseAuthInstance.currentUser?.photoUrl)
            .placeholder(R.drawable.selectphoto)
            .centerCrop()
            .into(btnSelectPhoto)
        btnSelectPhoto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose your avatar"), REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            val uri = data.data
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(btnSelectPhoto);

            uploadImageToFirebaseStorage(uri)
        }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri?) {
        if (uri != null) {
            val ref = FirebaseStorage.getInstance().getReference("/images/${fireBaseAuthInstance.currentUser?.uid}")
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("uploadImage", it.toString())
                        fireBaseAuthInstance.currentUser?.uid?.let { uid ->
                            val profileUpdates = userProfileChangeRequest {
                                photoUri = it
                            }
                            fireBaseAuthInstance.currentUser?.updateProfile(profileUpdates)
                        }
                    }
                }
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
            .setMessage("Back to main menu?")
            .setNegativeButton("Yes") { _, _ ->
                super.onBackPressed()
                finish()
            }
            .setPositiveButton("Dismiss") { dialog, _ -> dialog?.dismiss() }
        val dialog = builder.create();
        dialog.show()
    }
}
