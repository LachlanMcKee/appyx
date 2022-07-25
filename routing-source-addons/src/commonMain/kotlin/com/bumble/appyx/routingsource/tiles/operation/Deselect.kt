package com.bumble.appyx.routingsource.tiles.operation

import com.bumble.appyx.routingsource.CommonParcelize
import com.bumble.appyx.core.routing.RoutingElements
import com.bumble.appyx.core.routing.RoutingKey
import com.bumble.appyx.routingsource.tiles.Tiles
import com.bumble.appyx.routingsource.tiles.TilesElements

@CommonParcelize
data class Deselect<T : Any>(
    private val key: RoutingKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): RoutingElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.key == key && it.targetState == Tiles.TransitionState.SELECTED) {
                it.transitionTo(
                    newTargetState = Tiles.TransitionState.STANDARD,
                    operation = this
                )
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.deselect(key: RoutingKey<T>) {
    accept(Deselect(key))
}
