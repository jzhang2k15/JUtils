package com.jz.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jz.jutils.cache.CacheUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val mKey = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_put.setOnClickListener {
            val nextInt = Random.nextInt(100)
            Log.d(TAG_J, "随机成的值为: $nextInt")
            CacheUtils.sInstance.putData(mKey, nextInt)
        }

        btn_get.setOnClickListener {
            when (val data = CacheUtils.sInstance.getData(mKey)) {
                null -> tv_show.text = "没有读取到"
                else -> {
                    tv_show.text = "缓存里读取的值，data = $data"
                }
            }
        }
    }
}