package com.bumble.appyx.core.lifecycle

import android.util.Log

internal object LifecycleLogger : PlatformLifecycleObserver {

    private const val LOG_TAG = "Lifecycle"

    override fun onCreate(owner: PlatformLifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onCreate")
    }

    override fun onStart(owner: PlatformLifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onStart")
    }

    override fun onResume(owner: PlatformLifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onResume")
    }

    override fun onPause(owner: PlatformLifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onPause")
    }

    override fun onStop(owner: PlatformLifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onStop")
    }

    override fun onDestroy(owner: PlatformLifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onDestroy")
    }

}
