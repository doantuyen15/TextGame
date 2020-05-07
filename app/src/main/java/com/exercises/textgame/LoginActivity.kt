package com.exercises.textgame

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
            return
        }
        fireBaseAuthInstance.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "$user", Toast.LENGTH_SHORT).show()
                    //updateUI(user)
                } else {
                    val getError = task.getException()?.message
                    Toast.makeText(this, "$getError", Toast.LENGTH_LONG).show()
                    //updateUI(null)
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