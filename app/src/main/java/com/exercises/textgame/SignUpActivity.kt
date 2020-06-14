package com.exercises.textgame

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.exercises.textgame.models.UserInfo
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
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
        userRef.orderByChild(CHILD_USERNAME_KEY).equalTo(fullName).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                hideProgressBar()
                Toast.makeText(this@SignUpActivity, "${p0.toException()}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    hideProgressBar()
                    Toast.makeText(this@SignUpActivity, "Username already exists", Toast.LENGTH_LONG).show()
                } else {
                    //create user on server
                    createNewUser(fullName,email,password)
                }
            }
        })
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
                    saveUserToFireBase(fullName)
                } else {
                    val getError = task.exception?.message
                    Log.d("sign up", "===== sign up error: $getError")
                    hideProgressBar()
                }
            })
    }

    private fun saveUserToFireBase(fullName : String) {
        // ref: /users/uid
        val user = Firebase.auth.currentUser!!
        dbGetRefUser(user.uid).setValue(UserInfo(fullName))
            .addOnSuccessListener {
                //ref: /users/usernames/username
                //dbGetRefUser("usernames").child("$fullName").setValue(true)
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
            }
        //update user's profile
        val profileUpdates = userProfileChangeRequest {
            displayName = fullName
            //photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
        }
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Log.d(SignUpActivity::class.java.simpleName, "User profile updated.")
                } else {
                    val getError = task.exception?.message
                    Toast.makeText(this, "$getError", Toast.LENGTH_LONG).show()
                    //delete temp user if error
                }
            }
        hideProgressBar()
    }
//    companion object {
//        private const val TAG = "Signup"
//    }
}
