package com.bumble.appyx.core.lifecycle.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.Lifecycle.State.*
import androidx.lifecycle.LifecycleOwner
import com.bumble.appyx.core.lifecycle.PlatformLifecycle
import com.bumble.appyx.core.lifecycle.PlatformLifecycleOwner

internal fun LifecycleOwner.toPlatformLifecycleOwner(): PlatformLifecycleOwner =
    AndroidToPlatformLifecycleOwner(this)

fun PlatformLifecycleOwner.toAndroidLifecycleOwner(): LifecycleOwner =
    if (this is AndroidToPlatformLifecycleOwner) {
        this.androidOwner
    } else {
        PlatformToAndroidLifecycleOwner(this)
    }

fun PlatformLifecycle.toAndroidLifecycle(): Lifecycle =
    when (val lifecycle = this) {
        is AndroidPlatformLifecycleRegistry -> lifecycle.androidDelegate
        is AndroidPlatformLifecycle -> lifecycle.androidLifecycle
        else -> throw IllegalStateException("")
    }

fun Lifecycle.State.toPlatformState(): PlatformLifecycle.State =
    when (this) {
        DESTROYED -> PlatformLifecycle.State.DESTROYED
        INITIALIZED -> PlatformLifecycle.State.INITIALIZED
        CREATED -> PlatformLifecycle.State.CREATED
        STARTED -> PlatformLifecycle.State.STARTED
        RESUMED -> PlatformLifecycle.State.RESUMED
    }

fun Lifecycle.Event.toPlatformEvent(): PlatformLifecycle.Event =
    when (this) {
        ON_CREATE -> PlatformLifecycle.Event.ON_CREATE
        ON_START -> PlatformLifecycle.Event.ON_START
        ON_RESUME -> PlatformLifecycle.Event.ON_RESUME
        ON_PAUSE -> PlatformLifecycle.Event.ON_PAUSE
        ON_STOP -> PlatformLifecycle.Event.ON_STOP
        ON_DESTROY -> PlatformLifecycle.Event.ON_DESTROY
        ON_ANY -> PlatformLifecycle.Event.ON_ANY
    }

fun PlatformLifecycle.State.toAndroidState(): Lifecycle.State =
    when (this) {
        PlatformLifecycle.State.DESTROYED -> DESTROYED
        PlatformLifecycle.State.INITIALIZED -> INITIALIZED
        PlatformLifecycle.State.CREATED -> CREATED
        PlatformLifecycle.State.STARTED -> STARTED
        PlatformLifecycle.State.RESUMED -> RESUMED
    }

fun PlatformLifecycle.Event.toAndroidEvent(): Lifecycle.Event =
    when (this) {
        PlatformLifecycle.Event.ON_CREATE -> ON_CREATE
        PlatformLifecycle.Event.ON_START -> ON_START
        PlatformLifecycle.Event.ON_RESUME -> ON_RESUME
        PlatformLifecycle.Event.ON_PAUSE -> ON_PAUSE
        PlatformLifecycle.Event.ON_STOP -> ON_STOP
        PlatformLifecycle.Event.ON_DESTROY -> ON_DESTROY
        PlatformLifecycle.Event.ON_ANY -> ON_ANY
    }

private class AndroidToPlatformLifecycleOwner(val androidOwner: LifecycleOwner) :
    PlatformLifecycleOwner {
    override val lifecycle: PlatformLifecycle
        get() = AndroidPlatformLifecycle(androidOwner.lifecycle)
}

private class PlatformToAndroidLifecycleOwner(private val platformOwner: PlatformLifecycleOwner) :
    LifecycleOwner {
    override fun getLifecycle(): Lifecycle = platformOwner.lifecycle.toAndroidLifecycle()
}
