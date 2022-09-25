package com.bumble.appyx.navmodel.dualbackstack.operation

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack

sealed interface BackStackOperation<T> : Operation<T, DualBackStack.State>

fun <NavTarget, State> NavElement<NavTarget, State>.eitherState(state: State): Boolean =
    fromState == state || targetState == state
