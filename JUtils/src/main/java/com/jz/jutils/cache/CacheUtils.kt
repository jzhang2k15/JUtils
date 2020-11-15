package com.jz.jutils.cache

import android.util.Log
import java.util.*
import kotlin.collections.HashMap

/**
 * @author JohnZhang
 * @date 2020/11/12.
 * description：
 */
const val DEFAULT_CACHE_VALID_DURATION = 500// 默认的缓存有效期
const val DEFAULT_CACHE_CAPACITY = 10// 默认的缓存容量

class CacheUtils private constructor() {

    companion object {
        val sInstance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { CacheUtils() }
    }

    // 缓存的有效期
    var mValidDuration = DEFAULT_CACHE_VALID_DURATION

    // 缓存容量
    var mCapacity = DEFAULT_CACHE_CAPACITY

    // 存放 key 值不同的 CacheEntity 队列
    private val mMap by lazy { HashMap<Any, Queue<CacheEntity>>() }

    // 读写锁
    private val mLock by lazy { Any() }

    // 存入数据
    fun putData(key: Any, value: Any) {
        synchronized(mLock) {
            var queue = mMap[key]
            if (queue == null) {
                queue = LinkedList<CacheEntity>().also { mMap[key] = it }
            }
            queue.apply {
                if (size >= mCapacity) {
                    poll()
                    Log.d(TAG_J, "推陈...")
                }
                add(CacheEntity(value))
                Log.d(TAG_J, "出新...")
            }
        }
    }

    // 读取数据
    fun getData(key: Any): Any? {
        synchronized(mLock) {
            val queue = mMap[key] ?: return null
            queue.apply {
                var target: Any? = null
                while (isNotEmpty()) {
                    val cacheEntity = poll() ?: return null
                    if (cacheEntity.isValid { System.currentTimeMillis() - it <= mValidDuration }) {
                        Log.e(TAG_J, "找到了目标, $cacheEntity")
                        target = cacheEntity.mEntity
                        break
                    }
                    Log.d(TAG_J, "循环查找, $cacheEntity")
                }
                target?.let {
                    return it
                }
            }
            Log.d(TAG_J, "没有找到")
            return null
        }
    }

    /**
     * 用来包裹真正数据的类
     * 增加 timestamp 字段用来判断缓存是否有效
     */
    internal class CacheEntity(
            val mEntity: Any,
            private val mTimeStamp: Long = System.currentTimeMillis()
    ) {

        /**
         * 暴露 timestamp 出去来判断缓存的有效性
         *
         * @return 是否有效的缓存
         */
        fun isValid(param: (timeStamp: Long) -> Boolean) = param.invoke(mTimeStamp)

        override fun toString(): String {
            return "CacheEntity(mEntity=$mEntity, mTimeStamp=$mTimeStamp)"
        }

    }


}