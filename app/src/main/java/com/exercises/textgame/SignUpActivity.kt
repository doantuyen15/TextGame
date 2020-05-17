package com.exercises.textgame

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.signup.*


class SignUpActivity : BaseActivity() {
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
        userRef.orderByChild("username").equalTo(fullName).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                hideProgressBar()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    hideProgressBar()
                    Toast.makeText(this@SignUpActivity, "Username already exists", Toast.LENGTH_LONG).show()
                } else {
                    createNewUser(fullName,email,password)
                }
            }

        })
            //create user on server
//            .addOnSuccessListener {
//                fireBaseAuthInstance.createUserWithEmailAndPassword(email,password)
//                    .addOnCompleteListener(this, OnCompleteListener<AuthResult>() { task ->
//                        if (task.isSuccessful) {
//                            //save user info to server
//                            saveUserToFireBase(fireBaseAuthInstance.uid ?: "",fullName)
//                        } else {
//                            val getError = task.exception?.message
//                            Toast.makeText(this, "$getError", Toast.LENGTH_LONG).show()
//                            //delete temp user if error
//                            hideProgressBar()
//                        }
//                    })
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Username already exists", Toast.LENGTH_LONG).show()
//                hideProgressBar()
//            }
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
    private fun createNewUser(fullName: String, email: String, password: String) {
        fireBaseAuthInstance.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult>() { task ->
                if (task.isSuccessful) {
                    //save user info to server
                    saveUserToFireBase(fireBaseAuthInstance.uid!!, fullName)
                } else {
                    val getError = task.exception?.message
                    Toast.makeText(this, "$getError", Toast.LENGTH_LONG).show()
                    //delete temp user if error
                    hideProgressBar()
                }
            })
    }
    private fun saveUserToFireBase(uid : String, fullName : String) {
        // ref: /users/uid
        dbGetRefUser(uid).setValue(UserInfo(fullName))
            .addOnSuccessListener {
                //ref: /users/usernames/username
                //dbGetRefUser("usernames").child("$fullName").setValue(true)
                fetchUsers()
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_LONG).show()
                hideProgressBar()
                //finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
                hideProgressBar()
            }

    }
//    companion object {
//        private const val TAG = "Signup"
//    }
}
