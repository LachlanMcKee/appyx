package com.bumble.appyx.routingsource.tiles

import com.bumble.appyx.core.routing.onscreen.OnScreenStateResolver
import com.bumble.appyx.routingsource.tiles.Tiles.TransitionState

internal object TilesOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STANDARD,
            TransitionState.SELECTED -> true
            TransitionState.DESTROYED -> false
        }
}
