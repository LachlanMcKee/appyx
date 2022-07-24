package com.bumble.appyx.core.lifecycle

interface PlatformLifecycleRegistry : PlatformLifecycle {
    fun setCurrentState(state: PlatformLifecycle.State)
}
