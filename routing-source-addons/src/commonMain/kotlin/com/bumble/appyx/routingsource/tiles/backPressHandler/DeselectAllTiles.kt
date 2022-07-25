package com.bumble.appyx.routingsource.tiles.backPressHandler

import com.bumble.appyx.core.routing.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.routingsource.tiles.Tiles
import com.bumble.appyx.routingsource.tiles.TilesElements
import com.bumble.appyx.routingsource.tiles.operation.DeselectAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeselectAllTiles<Routing : Any> :
    BaseBackPressHandlerStrategy<Routing, Tiles.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        routingSource.elements.map(::areThereSelectedTiles)
    }

    private fun areThereSelectedTiles(elements: TilesElements<Routing>) =
        elements.any { it.targetState == Tiles.TransitionState.SELECTED }

    override fun onBackPressed() {
        routingSource.accept(DeselectAll())
    }
}
