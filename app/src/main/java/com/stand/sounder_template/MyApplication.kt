package com.stand.sounder_template

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle

class MyApplication : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startBeepService()

        finish()
    }

    private fun startBeepService() {
        val serviceIntent = Intent(this, BeepService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}