package com.hanto.hook

import android.app.Application
import com.hanto.hook.database.DatabaseModule

class MainApplication : Application() {

    private val TAG = "MainApplication"

    override fun onCreate() {
        super.onCreate()
        instance = this

        DatabaseModule.initialize(this)
//        applicationContext.deleteDatabase("hook_database")
    }


    companion object {
        private var instance: MainApplication? = null

        fun getInstance(): MainApplication {
            if (instance == null) {
                throw IllegalStateException("MainApplication is not initialized!")
            }
            return instance!!
        }

    }

}
