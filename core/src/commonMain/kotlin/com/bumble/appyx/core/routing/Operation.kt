package com.bumble.appyx.core.routing

import com.bumble.appyx.CommonParcelize
import com.bumble.appyx.CommonParcelable

interface Operation<Routing, State> :
        (RoutingElements<Routing, State>) -> RoutingElements<Routing, State>, CommonParcelable {

    fun isApplicable(elements: RoutingElements<Routing, State>): Boolean

    @CommonParcelize
    class Noop<Routing, State> : Operation<Routing, State> {

        override fun isApplicable(elements: RoutingElements<Routing, State>) = false

        override fun invoke(
            elements: RoutingElements<Routing, State>
        ): RoutingElements<Routing, State> = elements

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
