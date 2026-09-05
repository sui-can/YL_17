package com.stand.sounder_template

import android.content.Context
import android.content.res.AssetFileDescriptor

object BeepManager {

    private var audioNames: List<String>? = null

    private fun loadAssets(context: Context) {
        if (audioNames != null) return
        audioNames = context.assets.list("audio")?.toList()
        require(!audioNames.isNullOrEmpty()) { "assets/audio/ 为空" }
    }

    /**
     * 外部（Service）想播的时候，直接拿 AssetFileDescriptor 回去自己建 MediaPlayer
     * 生命周期完全由调用方控制
     */
    fun randomAssetFd(context: Context): AssetFileDescriptor {
        loadAssets(context)
        val name = audioNames!!.random()
        return context.assets.openFd("audio/$name")
    }
}