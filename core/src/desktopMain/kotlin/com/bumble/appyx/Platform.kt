package com.bumble.appyx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import com.bumble.appyx.core.lifecycle.*
import com.bumble.appyx.core.lifecycle.PlatformLifecycle.Event
import com.bumble.appyx.core.lifecycle.PlatformLifecycle.Event.*
import com.bumble.appyx.core.lifecycle.PlatformLifecycle.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import java.lang.IllegalStateException

actual interface CommonParcelable

actual fun createPlatformLifecycleRegistry(owner: PlatformLifecycleOwner): PlatformLifecycleRegistry =
    object : PlatformLifecycleRegistry {
        private val stateFlow: MutableStateFlow<State> = MutableStateFlow(State.CREATED)
        private val eventFlow: MutableStateFlow<Event> = MutableStateFlow(ON_CREATE)

        private val lifecycleObserverMap: MutableMap<PlatformLifecycleObserver, Job> = mutableMapOf()
        private val eventObserverMap: MutableMap<PlatformLifecycleEventObserver, Job> = mutableMapOf()

        override fun asFlow(): Flow<State> = stateFlow

        override fun setCurrentState(state: State) {
            stateFlow.value = state
        }

        override val currentState: State
            get() = stateFlow.value

        override val coroutineScope: CoroutineScope
            get() = checkNotNull(DesktopCoroutineScope) { "Desktop CoroutineScope not set" }

        override fun subscribe(
            onCreate: () -> Unit,
            onStart: () -> Unit,
            onResume: () -> Unit,
            onPause: () -> Unit,
            onStop: () -> Unit,
            onDestroy: () -> Unit
        ) {
            addObserver(
                object : PlatformLifecycleObserver {
                    override fun onCreate(owner: PlatformLifecycleOwner) {
                        onCreate()
                    }

                    override fun onStart(owner: PlatformLifecycleOwner) {
                        onStart()
                    }

                    override fun onResume(owner: PlatformLifecycleOwner) {
                        onResume()
                    }

                    override fun onPause(owner: PlatformLifecycleOwner) {
                        onPause()
                    }

                    override fun onStop(owner: PlatformLifecycleOwner) {
                        onStop()
                    }

                    override fun onDestroy(owner: PlatformLifecycleOwner) {
                        onDestroy()
                    }
                }
            )
        }

        override fun addObserver(observer: PlatformLifecycleObserver) {
            val job = eventFlow
                .onEach { event ->
                    when (event) {
                        ON_CREATE -> observer.onCreate(owner)
                        ON_START -> observer.onStart(owner)
                        ON_RESUME -> observer.onResume(owner)
                        ON_PAUSE -> observer.onPause(owner)
                        ON_STOP -> observer.onStop(owner)
                        ON_DESTROY -> observer.onDestroy(owner)
                        ON_ANY -> throw IllegalArgumentException("ON_ANY must not been send by anybody")
                    }
                }
                .launchIn(coroutineScope)

            lifecycleObserverMap[observer] = job
        }

        override fun addObserver(observer: PlatformLifecycleEventObserver) {
            val job = eventFlow
                .onEach { event ->
                    observer.onStateChanged(owner, event)
                }
                .launchIn(coroutineScope)

            eventObserverMap[observer] = job
        }

        override fun removeObserver(observer: PlatformLifecycleObserver) {
            lifecycleObserverMap[observer]?.cancel()
        }

        override fun removeObserver(observer: PlatformLifecycleEventObserver) {
            eventObserverMap[observer]?.cancel()
        }
    }

@Composable
actual fun currentLifecycle(): PlatformLifecycle {
    throw IllegalStateException("Do not call current lifecycle for desktop (for now at least)")
}

actual fun createLifecycleOwnerProvider(owner: PlatformLifecycleOwner): ProvidedValue<*>? =
    null

var DesktopCoroutineScope: CoroutineScope? = null
