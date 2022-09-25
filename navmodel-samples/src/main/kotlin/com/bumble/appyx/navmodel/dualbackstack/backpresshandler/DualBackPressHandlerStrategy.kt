package com.bumble.appyx.navmodel.dualbackstack.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack

interface DualBackPressHandlerStrategy<NavTarget : Any> :
    BackPressHandlerStrategy<NavTarget, DualBackStack.State> {

    fun setTwoPanelsEnabledFunction(enabledFunc: () -> Boolean)
}
