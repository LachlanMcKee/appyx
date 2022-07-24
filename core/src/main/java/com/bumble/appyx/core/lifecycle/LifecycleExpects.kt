package com.bumble.appyx.core.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.bumble.appyx.core.lifecycle.android.AndroidPlatformLifecycle
import com.bumble.appyx.core.lifecycle.android.AndroidPlatformLifecycleRegistry

object LifecycleExpects {
    // TODO: This needs to be a multiplatform expect to return the correct lifecycle.
    // For Android this should delegate to: LocalLifecycleOwner.current.lifecycle
    @Composable
    fun currentLifecycle(): PlatformLifecycle =
        AndroidPlatformLifecycle(LocalLifecycleOwner.current.lifecycle)

}

// Needs to be an expects as well!
fun createPlatformLifecycleRegistry(owner: PlatformLifecycleOwner): PlatformLifecycleRegistry {
    return AndroidPlatformLifecycleRegistry(owner)
}
