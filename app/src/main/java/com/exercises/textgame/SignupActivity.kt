package com.exercises.textgame

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
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
        Create_Login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
    private fun performRegister(email : String, password : String, fullName : String){

        //validate form
        if (!validateForm(email,password,fullName)) {
            hideProgressBar()
            return
        }

        //check if username already exists on server
        val user = UserInfo(null)
        getRef(fullName).setValue(user)

            //create temp user on server
            .addOnSuccessListener {
                fireBaseAuthInstance.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, OnCompleteListener<AuthResult>() { task ->
                        if (task.isSuccessful) {
                            //on success sign up
                            saveUserToFireBase(fireBaseAuthInstance.uid ?: "",fullName)
                        } else {
                            val getError = task.getException()?.message
                            Toast.makeText(this, "$getError", Toast.LENGTH_LONG).show()
                            //delete temp user if error
                            hideProgressBar()
                        }
                    })
            }
            .addOnFailureListener {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_LONG).show()
                hideProgressBar()
            }
    }
    private fun validateForm(email : String, password : String, fullName : String): Boolean {

        valid = true
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

        if (TextUtils.isEmpty(fullName)) {
            Create_FullName.error = "Required."
            valid = false
        } else {
            Create_FullName.error = null
        }
        return valid
   }
    private fun saveUserToFireBase(uid : String, fullName : String) {
        val ref = fireBaseDataBaseInstance.getReference("/users/$fullName")
        val user = UserInfo(uid)
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_LONG).show()
                hideProgressBar()
                finish()
            }
            .addOnFailureListener {
                // can't be fail here :D
            }

    }
//    companion object {
//        private const val TAG = "Signup"
//    }
}
