package com.exercises.textgame

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.login.*
import java.sql.DatabaseMetaData


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        validate.setOnClickListener{
            val email = Email.text.toString()
            val password = Password.text.toString()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_LONG)
                }
        }
        signupwithgg.setOnClickListener{
            val email = Email.text.toString()
            val password = Password.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_LONG)
                }
        }
        private lateinit var database: DatabaseReference
        database = Firebase.database.reference
        var text = database.getReference("User")
        text.setValue("hello")
    }
}