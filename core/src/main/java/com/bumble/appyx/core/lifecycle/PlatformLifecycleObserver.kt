package com.bumble.appyx.core.lifecycle

interface PlatformLifecycleObserver {
    fun onCreate(owner: PlatformLifecycleOwner) {}
    fun onStart(owner: PlatformLifecycleOwner) {}
    fun onResume(owner: PlatformLifecycleOwner) {}
    fun onPause(owner: PlatformLifecycleOwner) {}
    fun onStop(owner: PlatformLifecycleOwner) {}
    fun onDestroy(owner: PlatformLifecycleOwner) {}
}
