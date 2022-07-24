package com.bumble.appyx.core.lifecycle

import com.bumble.appyx.createPlatformLifecycleRegistry

internal class NodeLifecycleImpl(owner: PlatformLifecycleOwner) : NodeLifecycle {

    private val lifecycleRegistry = createPlatformLifecycleRegistry(owner)

    override val lifecycle: PlatformLifecycle
        get() = lifecycleRegistry

    override fun updateLifecycleState(state: PlatformLifecycle.State) {
        lifecycleRegistry.setCurrentState(state)
    }

}
