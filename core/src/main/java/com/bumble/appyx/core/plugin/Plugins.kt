package com.bumble.appyx.core.plugin

import com.bumble.appyx.core.lifecycle.PlatformLifecycle
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.state.MutableSavedStateMap

interface Plugin


inline fun <reified P : Plugin> Node.plugins(): List<P> =
    this.plugins.filterIsInstance(P::class.java)

interface NodeAware<N : Node> : Plugin {
    val node: N

    fun init(node: N) {}
}

interface NodeLifecycleAware : Plugin {
    fun onCreate(lifecycle: PlatformLifecycle) {}
}

interface UpNavigationHandler : Plugin {
    fun handleUpNavigation() = false
}

fun interface Destroyable : Plugin {
    fun destroy()
}

// TODO: It is not a plugin! Handled only in router!
interface BackPressHandler : Plugin {
    fun onBackPressed()
}

/**
 * Bundle for future state restoration.
 * Result should be supported by [androidx.compose.runtime.saveable.SaverScope.canBeSaved].
 */
interface SavesInstanceState : Plugin {
    fun saveInstanceState(state: MutableSavedStateMap) {}
}
