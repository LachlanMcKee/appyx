package com.bumble.appyx.routingsource.modal.operation

import com.bumble.appyx.routingsource.CommonParcelize
import com.bumble.appyx.routingsource.CommonRawValue
import com.bumble.appyx.routingsource.modal.Modal
import com.bumble.appyx.routingsource.modal.Modal.TransitionState.CREATED
import com.bumble.appyx.routingsource.modal.ModalElement
import com.bumble.appyx.routingsource.modal.ModalElements
import com.bumble.appyx.core.routing.RoutingKey

@CommonParcelize
data class Add<T : Any>(
    private val element: @CommonRawValue T
) : ModalOperation<T> {

    override fun isApplicable(elements: ModalElements<T>) = true

    override fun invoke(elements: ModalElements<T>): ModalElements<T> {
        return elements + ModalElement(
            key = RoutingKey(element),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )
    }
}

fun <T : Any> Modal<T>.add(element: T) {
    accept(Add(element))
}
