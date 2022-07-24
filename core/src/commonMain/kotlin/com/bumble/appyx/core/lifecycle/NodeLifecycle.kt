package com.bumble.appyx.core.lifecycle

interface NodeLifecycle: PlatformLifecycleOwner {

    fun updateLifecycleState(state: PlatformLifecycle.State)

}
