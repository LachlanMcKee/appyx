package com.bumble.appyx.core.lifecycle

import com.bumble.appyx.createPlatformLifecycleRegistry

/**
 * Combines multiple lifecycles and provides a minimum of their states.
 *
 * For example:
 * - RESUMED + STARTED + RESUMED -> STARTED
 * - CREATED + RESUMED + DESTROYED -> DESTROYED
 * - INITIALIZED + DESTROYED -> DESTROYED
 */
internal class MinimumCombinedLifecycle(
    vararg lifecycles: PlatformLifecycle,
) : PlatformLifecycleOwner {
    private val registry = createPlatformLifecycleRegistry(this)
    private val lifecycles = ArrayList<PlatformLifecycle>()

    init {
        /*
        Sort list to avoid unnecessary state jumps.
        If Lifecycle(RESUMED) + Lifecycle(DESTROYED) is passed,
        then we should have the final state in DESTROYED state without additional jumping to RESUMED.
         */
        lifecycles.sortedBy { it.currentState }.forEach { manage(it) }
    }

    override val lifecycle: PlatformLifecycle
        get() = registry

    fun manage(lifecycle: PlatformLifecycle) {
        lifecycles += lifecycle
        lifecycle.addObserver(object : PlatformLifecycleObserver {
            override fun onCreate(owner: PlatformLifecycleOwner) {
                update()
            }

            override fun onStart(owner: PlatformLifecycleOwner) {
                update()
            }

            override fun onResume(owner: PlatformLifecycleOwner) {
                update()
            }

            override fun onPause(owner: PlatformLifecycleOwner) {
                update()
            }

            override fun onStop(owner: PlatformLifecycleOwner) {
                update()
            }

            override fun onDestroy(owner: PlatformLifecycleOwner) {
                update()
            }
        })
        update()
    }

    private fun update() {
        lifecycles
            .minByOrNull { it.currentState }
            ?.takeIf { it.currentState != PlatformLifecycle.State.INITIALIZED }
            ?.also { registry.setCurrentState(it.currentState) }
    }

}
