//package com.exercises.textgame
//
//import android.annotation.SuppressLint
//import android.app.Application
//import com.google.firebase.database.FirebaseDatabase
//
//
//@SuppressLint("Registered")
//open class FirebaseHandler : Application() {
//
//    override fun onCreate() {
//        super.onCreate()
//
//        val firebase = FirebaseDatabase.getInstance()
//        firebase.setPersistenceEnabled(true)
//        val userRef = firebase.getReference("/users")
//        userRef.keepSynced(true)
////        val roomRef = firebase.getReference("/gamerooms")
////        roomRef.keepSynced(true)
//    }
//}
