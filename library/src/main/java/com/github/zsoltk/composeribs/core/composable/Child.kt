package com.github.zsoltk.composeribs.core.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.zsoltk.composeribs.core.node.ParentNode
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingElements
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.source.backstack.JumpToEndTransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionBounds
import com.github.zsoltk.composeribs.core.routing.transition.TransitionDescriptor
import com.github.zsoltk.composeribs.core.routing.transition.TransitionHandler
import com.github.zsoltk.composeribs.core.routing.transition.TransitionParams

@Composable
fun <Routing : Any, State> ParentNode<Routing>.Child(
    routingElement: RoutingElement<Routing, State>,
    transitionHandler: TransitionHandler<Routing, State> = JumpToEndTransitionHandler(),
    decorator: @Composable ChildTransitionScope<State>.(transitionModifier: Modifier, child: @Composable () -> Unit) -> Unit = { modifier, child ->
        Box(modifier = modifier) {
            child()
        }
    }
) {
    val childEntry = remember { childOrCreate(routingElement.key) }

    key(routingElement.key) {
        BoxWithConstraints {
            val transitionScope =
                transitionHandler.handle(
                    descriptor = TransitionDescriptor(
                        params = TransitionParams(
                            bounds = TransitionBounds(
                                width = maxWidth,
                                height = maxHeight
                            )
                        ),
                        operation = routingElement.operation,
                        element = routingElement.key.routing,
                        fromState = routingElement.fromState,
                        toState = routingElement.targetState
                    ),
                    onTransitionFinished = {
                        routingSource.onTransitionFinished(routingElement.key)
                    }
                )
            transitionScope.decorator(
                transitionModifier = transitionScope.transitionModifier,
                child = { childEntry.node.Compose() },
            )
        }

    }
}

@Composable
fun <R, S> RoutingSource<R, S>?.childrenAsState(): State<RoutingElements<R, out S>> =
    if (this != null) {
        elements.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
    }
