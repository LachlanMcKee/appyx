package com.bumble.appyx.routingsource.modal.backpresshandler

import com.bumble.appyx.core.routing.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.routingsource.modal.Modal.TransitionState
import com.bumble.appyx.routingsource.modal.Modal.TransitionState.FULL_SCREEN
import com.bumble.appyx.routingsource.modal.ModalElements
import com.bumble.appyx.routingsource.modal.operation.Revert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RevertBackPressHandler<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        routingSource.elements.map(::areThereFullScreenElements)
    }

    private fun areThereFullScreenElements(elements: ModalElements<Routing>) =
        elements.any { it.targetState == FULL_SCREEN }

    override fun onBackPressed() {
        routingSource.accept(Revert())
    }
}
