package com.bumble.appyx.routingsource.promoter.routingsource

import com.bumble.appyx.routingsource.promoter.routingsource.Promoter.TransitionState
import com.bumble.appyx.core.routing.onscreen.OnScreenStateResolver

internal object PromoterOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.DESTROYED -> false
            else -> true
        }
}
