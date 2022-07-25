package com.bumble.appyx.routingsource.modal.operation

import com.bumble.appyx.routingsource.CommonParcelize
import com.bumble.appyx.routingsource.modal.Modal
import com.bumble.appyx.routingsource.modal.Modal.TransitionState.FULL_SCREEN
import com.bumble.appyx.routingsource.modal.Modal.TransitionState.MODAL
import com.bumble.appyx.routingsource.modal.ModalElements

@CommonParcelize
class Revert<T : Any> : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>): Boolean =
        true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements.map {
            when (it.targetState) {
                FULL_SCREEN -> {
                    it.transitionTo(
                        newTargetState = MODAL,
                        operation = this
                    )
                }
                MODAL -> {
                    it.transitionTo(
                        newTargetState = FULL_SCREEN,
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }
    }
}

fun <T : Any> Modal<T>.revert() {
    accept(Revert())
}
