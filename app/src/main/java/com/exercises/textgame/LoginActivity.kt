package com.exercises.textgame

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.login.*


class LoginActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        setProgressBar(progressBar)


        login_validate.setOnClickListener{
            showProgressBar()
            performLogin(Email.text.toString(),Password.text.toString())
        }
        sigup_text.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }

    }

//    private fun showProgressBar() {
//        progressBar?.visibility = View.VISIBLE
//    }
//
//    private fun hideProgressBar() {
//        progressBar?.visibility = View.INVISIBLE
//    }


    private  fun performLogin(email: String, password: String){
        if (!validateForm(email,password)) {
            hideProgressBar()
            return
        }
        fireBaseAuthInstance.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fetchUsers()
                    startActivity(Intent(this,MainActivity::class.java))
                } else {
                    val getError = task.exception?.message
                    Toast.makeText(this, "$getError", Toast.LENGTH_LONG).show()
                }
                hideProgressBar()
            }
    }
    private fun validateForm(email : String, password : String): Boolean {
        valid = true
        if (TextUtils.isEmpty(email)) {
            Email.error = "Required."
            valid = false
        } else {
            Email.error = null
        }

        if (TextUtils.isEmpty(password)) {
            Password.error = "Required."
            valid = false
        } else {
            Password.error = null
        }

        return valid
    }
}