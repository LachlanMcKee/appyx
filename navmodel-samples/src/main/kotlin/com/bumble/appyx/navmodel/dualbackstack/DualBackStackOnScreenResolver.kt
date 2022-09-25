package com.bumble.appyx.navmodel.dualbackstack

import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State

object DualBackStackOnScreenResolver : OnScreenStateResolver<State> {

    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.Created1,
            State.Created2,
            State.StashedInBackStack1,
            State.StashedInBackStack2,
            State.Destroyed1,
            State.Destroyed2 -> false
            State.Active1,
            State.Active2 -> true
        }
}
