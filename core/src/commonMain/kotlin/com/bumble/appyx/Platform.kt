package com.bumble.appyx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import com.bumble.appyx.core.lifecycle.PlatformLifecycle
import com.bumble.appyx.core.lifecycle.PlatformLifecycleOwner
import com.bumble.appyx.core.lifecycle.PlatformLifecycleRegistry

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class CommonParcelize()

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
expect annotation class CommonRawValue()

expect interface CommonParcelable

expect fun createPlatformLifecycleRegistry(owner: PlatformLifecycleOwner): PlatformLifecycleRegistry

@Composable
expect fun currentLifecycle(): PlatformLifecycle

expect fun createLifecycleOwnerProvider(owner: PlatformLifecycleOwner): ProvidedValue<*>

