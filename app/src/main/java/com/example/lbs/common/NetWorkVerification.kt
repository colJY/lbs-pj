package com.example.lbs.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetWorkVerification {
    companion object  {
        fun isNetworkAvailable(context: Context): Boolean { // singleTon으로 구현
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = cm.activeNetwork ?: return false
                val networkCapabilities = cm.getNetworkCapabilities(nw) ?: return false
                return when {
                    //현재 단말기의 연결유무(Wifi, Data 통신)
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                    //단말기가 아닐경우(ex:: IoT 장비등)
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    //블루투스 인터넷 연결유뮤
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION")
                return cm.activeNetworkInfo?.isConnected ?: false
            }
        }
    }
}