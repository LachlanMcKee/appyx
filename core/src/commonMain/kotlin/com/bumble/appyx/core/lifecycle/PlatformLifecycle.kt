package com.bumble.appyx.core.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PlatformLifecycle {
    val currentState: State
    val isDestroyed: Boolean get() = currentState == State.DESTROYED
    val coroutineScope: CoroutineScope

    fun asFlow(): Flow<State>

    fun subscribe(
        onCreate: () -> Unit = {},
        onStart: () -> Unit = {},
        onResume: () -> Unit = {},
        onPause: () -> Unit = {},
        onStop: () -> Unit = {},
        onDestroy: () -> Unit = {}
    )

    fun addObserver(observer: PlatformLifecycleObserver)
    fun removeObserver(observer: PlatformLifecycleObserver)

    fun addObserver(observer: PlatformLifecycleEventObserver)
    fun removeObserver(observer: PlatformLifecycleEventObserver)

    enum class State {
        DESTROYED,
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED;

        fun isAtLeast(state: State): Boolean {
            return compareTo(state) >= 0
        }
    }

    enum class Event {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY;
    }

}
