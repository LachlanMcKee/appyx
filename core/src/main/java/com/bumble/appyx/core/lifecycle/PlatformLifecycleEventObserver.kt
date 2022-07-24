package com.bumble.appyx.core.lifecycle

fun interface PlatformLifecycleEventObserver {
    fun onStateChanged(source: PlatformLifecycleOwner, event: PlatformLifecycle.Event)
}
