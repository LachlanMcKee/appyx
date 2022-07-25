package com.bumble.appyx.core.lifecycle.android

import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.core.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

internal class AndroidPlatformLifecycleRegistry(owner: PlatformLifecycleOwner) :
    PlatformLifecycleRegistry {
    // Keep a hard reference to avoid garbage collection
    private val androidLifecycleOwner = owner.toAndroidLifecycleOwner()
    val androidDelegate = LifecycleRegistry(androidLifecycleOwner)

    private val platformDelegate = AndroidPlatformLifecycle(androidDelegate)

    override fun setCurrentState(state: PlatformLifecycle.State) {
        androidDelegate.currentState = state.toAndroidState()
    }

    override val currentState: PlatformLifecycle.State
        get() = platformDelegate.currentState

    override val coroutineScope: CoroutineScope =
        androidDelegate.coroutineScope

    override fun asFlow(): Flow<PlatformLifecycle.State> =
        platformDelegate.asFlow()

    override fun subscribe(
        onCreate: () -> Unit,
        onStart: () -> Unit,
        onResume: () -> Unit,
        onPause: () -> Unit,
        onStop: () -> Unit,
        onDestroy: () -> Unit
    ) {
        platformDelegate.subscribe(onCreate, onStart, onResume, onPause, onStop, onDestroy)
    }

    override fun addObserver(observer: PlatformLifecycleObserver) {
        platformDelegate.addObserver(observer)
    }

    override fun addObserver(observer: PlatformLifecycleEventObserver) {
        platformDelegate.addObserver(observer)
    }

    override fun removeObserver(observer: PlatformLifecycleObserver) {
        platformDelegate.removeObserver(observer)
    }

    override fun removeObserver(observer: PlatformLifecycleEventObserver) {
        platformDelegate.removeObserver(observer)
    }

}
