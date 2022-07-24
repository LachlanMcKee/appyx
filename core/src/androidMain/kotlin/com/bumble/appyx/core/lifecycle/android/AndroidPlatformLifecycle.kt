package com.bumble.appyx.core.lifecycle.android

import androidx.compose.runtime.Composable
import androidx.lifecycle.*
import com.bumble.appyx.core.lifecycle.PlatformLifecycle
import com.bumble.appyx.core.lifecycle.PlatformLifecycleEventObserver
import com.bumble.appyx.core.lifecycle.PlatformLifecycleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal class AndroidPlatformLifecycle(val androidLifecycle: Lifecycle) : PlatformLifecycle {
    private val lifecycleObserverMap: MutableMap<PlatformLifecycleObserver, DefaultLifecycleObserver> =
        mutableMapOf()
    private val eventObserverMap: MutableMap<PlatformLifecycleEventObserver, LifecycleEventObserver> =
        mutableMapOf()

    override val currentState: PlatformLifecycle.State
        get() = androidLifecycle.currentState.toPlatformState()

    @Composable
    override fun coroutineScope(): CoroutineScope =
        androidLifecycle.coroutineScope

    override fun asFlow(): Flow<PlatformLifecycle.State> =
        callbackFlow {
            val observer = LifecycleEventObserver { source, _ ->
                trySend(source.lifecycle.currentState.toPlatformState())
            }
            trySend(currentState)
            androidLifecycle.addObserver(observer)
            awaitClose { androidLifecycle.removeObserver(observer) }
        }

    override fun subscribe(
        onCreate: () -> Unit,
        onStart: () -> Unit,
        onResume: () -> Unit,
        onPause: () -> Unit,
        onStop: () -> Unit,
        onDestroy: () -> Unit
    ) {
        androidLifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    onCreate()
                }

                override fun onStart(owner: LifecycleOwner) {
                    onStart()
                }

                override fun onResume(owner: LifecycleOwner) {
                    onResume()
                }

                override fun onPause(owner: LifecycleOwner) {
                    onPause()
                }

                override fun onStop(owner: LifecycleOwner) {
                    onStop()
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    onDestroy()
                }
            }
        )
    }

    override fun addObserver(observer: PlatformLifecycleObserver) {
        val androidObserver = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                observer.onCreate(owner.toPlatformLifecycleOwner())
            }

            override fun onStart(owner: LifecycleOwner) {
                observer.onStart(owner.toPlatformLifecycleOwner())
            }

            override fun onResume(owner: LifecycleOwner) {
                observer.onResume(owner.toPlatformLifecycleOwner())
            }

            override fun onPause(owner: LifecycleOwner) {
                observer.onPause(owner.toPlatformLifecycleOwner())
            }

            override fun onStop(owner: LifecycleOwner) {
                observer.onStop(owner.toPlatformLifecycleOwner())
            }

            override fun onDestroy(owner: LifecycleOwner) {
                observer.onDestroy(owner.toPlatformLifecycleOwner())
            }
        }
        lifecycleObserverMap[observer] = androidObserver
        androidLifecycle.addObserver(androidObserver)
    }

    override fun addObserver(observer: PlatformLifecycleEventObserver) {
        val androidObserver = LifecycleEventObserver { source, event ->
            observer.onStateChanged(
                source.toPlatformLifecycleOwner(),
                event.toPlatformEvent()
            )
        }
        eventObserverMap[observer] = androidObserver
        androidLifecycle.addObserver(androidObserver)
    }

    override fun removeObserver(observer: PlatformLifecycleObserver) {
        val androidObserver = lifecycleObserverMap.remove(observer)
        if (androidObserver != null) {
            androidLifecycle.removeObserver(androidObserver)
        }
    }

    override fun removeObserver(observer: PlatformLifecycleEventObserver) {
        val androidObserver = eventObserverMap.remove(observer)
        if (androidObserver != null) {
            androidLifecycle.removeObserver(androidObserver)
        }
    }
}
