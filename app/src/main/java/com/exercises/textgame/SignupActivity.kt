package com.exercises.textgame

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.signup.*


class SignupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        setProgressBar(Create_progressBar)

        Create_validate.setOnClickListener{
            showProgressBar()
            performRegister(Create_Email.text.toString(), Create_Password.text.toString(), Create_FullName.text.toString())
        }
    }
    private fun performRegister(email : String, password : String, fullName : String){
        if (!validateForm(email,password,fullName)) {
            hideProgressBar()
            return
        }
        fireBaseAuthInstance.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult>() { task ->
                if (task.isSuccessful) {
                    //val user = auth.currentUser
                    saveUserToFireBase(fullName, fireBaseAuthInstance.uid ?: "")
                    //updateUI(user)
                } else {
                    var getError = task.getException()?.message
                    Toast.makeText(this, "$getError", Toast.LENGTH_LONG).show()
                }
                hideProgressBar()
            })
    }
    private fun validateForm(email : String, password : String, fullName : String): Boolean {
        var valid = true

        if (TextUtils.isEmpty(email)) {
            Create_Email.error = "Required."
            valid = false
        } else {
            Create_Email.error = null
        }

        if (TextUtils.isEmpty(password)) {
            Create_Password.error = "Required."
            valid = false
        } else {
            Create_Password.error = null
        }

        if (checkUserNameAlreadyExist(fullName)){
            Toast.makeText(this, "Username already exists!", Toast.LENGTH_LONG).show()
            valid = false
        }

        if (TextUtils.isEmpty(fullName)) {
            Create_FullName.error = "Required."
            valid = false
        } else {
            Create_FullName.error = null
        }
        Log.d(TAG, "$valid")
        return valid
   }
    private fun saveUserToFireBase(fullName : String, uid : String){
        val ref = fireBaseDataBaseInstance.getReference("/users/$fullName")
        val user = UserInfo(uid, fullName)
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_LONG).show()
                hideProgressBar()
                finish()
            }
    }

    companion object {
        private const val TAG = "Signup"
    }
}
