package com.bumble.appyx

import android.os.Parcelable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.bumble.appyx.core.lifecycle.PlatformLifecycle
import com.bumble.appyx.core.lifecycle.PlatformLifecycleOwner
import com.bumble.appyx.core.lifecycle.PlatformLifecycleRegistry
import com.bumble.appyx.core.lifecycle.android.AndroidPlatformLifecycle
import com.bumble.appyx.core.lifecycle.android.AndroidPlatformLifecycleRegistry
import com.bumble.appyx.core.lifecycle.android.toAndroidLifecycleOwner
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

actual typealias CommonParcelize = Parcelize

actual typealias CommonParcelable = Parcelable

actual typealias CommonRawValue = RawValue

actual fun createPlatformLifecycleRegistry(owner: PlatformLifecycleOwner): PlatformLifecycleRegistry {
    return AndroidPlatformLifecycleRegistry(owner)
}

@Composable
actual fun currentLifecycle(): PlatformLifecycle =
    AndroidPlatformLifecycle(LocalLifecycleOwner.current.lifecycle)

actual fun createLifecycleOwnerProvider(owner: PlatformLifecycleOwner): ProvidedValue<*>? =
    LocalLifecycleOwner provides owner.toAndroidLifecycleOwner()
