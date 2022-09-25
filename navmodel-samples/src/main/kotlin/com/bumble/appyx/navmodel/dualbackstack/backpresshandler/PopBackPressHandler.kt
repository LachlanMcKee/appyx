package com.bumble.appyx.navmodel.dualbackstack.backpresshandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State
import com.bumble.appyx.navmodel.dualbackstack.DualBackStackElements
import com.bumble.appyx.navmodel.dualbackstack.operation.Pop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PopBackPressHandler<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, State>(),
    DualBackPressHandlerStrategy<NavTarget> {

    private lateinit var twoPanelsEnabledFunc: () -> Boolean

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areThereStashedElements)
    }

    private fun areThereStashedElements(elements: DualBackStackElements<NavTarget>) =
        elements.any {
            it.targetState == State.StashedInBackStack1 ||
                    it.targetState == State.StashedInBackStack2
        }

    override fun onBackPressed() {
        navModel.accept(Pop(twoPanelsEnabledFunc()))
    }

    override fun setTwoPanelsEnabledFunction(enabledFunc: () -> Boolean) {
        twoPanelsEnabledFunc = enabledFunc
    }
}
