package com.example.lbs.common

import android.app.Application

class LBSApplication: Application() {
    init {
        instance = this
    }
    companion object {
        private var instance: LBSApplication? = null
        fun applicationContext() : LBSApplication {
            return instance as LBSApplication
        }
    }
}