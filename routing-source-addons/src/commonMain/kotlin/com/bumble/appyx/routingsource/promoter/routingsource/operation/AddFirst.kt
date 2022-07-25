package com.bumble.appyx.routingsource.promoter.routingsource.operation

import com.bumble.appyx.routingsource.CommonParcelize
import com.bumble.appyx.routingsource.CommonRawValue
import com.bumble.appyx.routingsource.promoter.routingsource.Promoter
import com.bumble.appyx.routingsource.promoter.routingsource.Promoter.TransitionState.CREATED
import com.bumble.appyx.routingsource.promoter.routingsource.PromoterElement
import com.bumble.appyx.routingsource.promoter.routingsource.PromoterElements
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey

@CommonParcelize
data class AddFirst<T : Any>(
    private val element: @CommonRawValue T
) : PromoterOperation<T> {

    override fun isApplicable(elements: PromoterElements<T>): Boolean =
        true

    override fun invoke(
        elements: PromoterElements<T>,
    ): RoutingElements<T, Promoter.TransitionState> {
        val new = PromoterElement(
            key = RoutingKey(element),
            fromState = CREATED,
            targetState = CREATED,
            operation = this
        )

        return listOf(new) + elements
    }
}

fun <T : Any> Promoter<T>.addFirst(element: T) {
    accept(AddFirst(element))
}
