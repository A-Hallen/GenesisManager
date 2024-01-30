package com.hallen.genesismanager

import android.app.Application
import com.hallen.genesismanager.system.Prefs
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Aplication: Application(){
    companion object {
        lateinit var prefs: Prefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
        Logger.addLogAdapter(AndroidLogAdapter())
    }
}