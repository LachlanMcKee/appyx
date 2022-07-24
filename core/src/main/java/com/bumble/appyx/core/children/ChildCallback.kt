package com.bumble.appyx.core.children

import com.bumble.appyx.core.lifecycle.PlatformLifecycle

typealias ChildrenCallback<T1, T2> = (commonLifecycle: PlatformLifecycle, child1: T1, child2: T2) -> Unit

typealias ChildCallback<T> = (commonLifecycle: PlatformLifecycle, child: T) -> Unit
