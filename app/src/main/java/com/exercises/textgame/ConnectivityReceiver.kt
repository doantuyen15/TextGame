//package com.exercises.textgame
//
//import android.net.ConnectivityManager
//import android.annotation.SuppressLint
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//
//class ConnectivityReceiver : BroadcastReceiver() {
//
//    @SuppressLint("UnsafeProtectedBroadcastReceiver")
//    override fun onReceive(context: Context, arg1: Intent) {
//        connectivityReceiverListener?.onNetworkConnectionChanged(checkNetworkState(context))
//    }
//
//    private fun checkNetworkState(context: Context): Boolean {
//        //for api lv < 24
//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//        return networkInfo != null && networkInfo.isConnectedOrConnecting
//    }
//
//    interface ConnectivityReceiverListener {
//        fun onNetworkConnectionChanged(isConnected: Boolean)
//    }
//
//    companion object {
//        var connectivityReceiverListener: ConnectivityReceiverListener? = null
//    }
//}