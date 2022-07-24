package com.bumble.appyx.core.routing

interface Operation<Routing, State> :
        (RoutingElements<Routing, State>) -> RoutingElements<Routing, State> {

    fun isApplicable(elements: RoutingElements<Routing, State>): Boolean

    class Noop<Routing, State> : Operation<Routing, State> {

        override fun isApplicable(elements: RoutingElements<Routing, State>) = false

        override fun invoke(
            elements: RoutingElements<Routing, State>
        ): RoutingElements<Routing, State> = elements

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}
