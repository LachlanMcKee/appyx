package com.bumble.appyx.core.routing

import com.bumble.appyx.CommonParcelize
import com.bumble.appyx.CommonRawValue
import com.bumble.appyx.CommonParcelable
import androidx.compose.runtime.Immutable

@CommonParcelize
@Immutable
class RoutingElement<Routing, State> private constructor(
    val key: @CommonRawValue RoutingKey<Routing>,
    val fromState: @CommonRawValue State,
    val targetState: @CommonRawValue State,
    val operation: @CommonRawValue Operation<Routing, State>,
    val transitionHistory: List<Pair<State, State>>
) : CommonParcelable {
    constructor(
        key: @CommonRawValue RoutingKey<Routing>,
        fromState: @CommonRawValue State,
        targetState: @CommonRawValue State,
        operation: @CommonRawValue Operation<Routing, State>,
    ) : this(
        key,
        fromState,
        targetState,
        operation,
        if (fromState == targetState) emptyList() else listOf(fromState to targetState)
    )

    fun transitionTo(
        newTargetState: @CommonRawValue State,
        operation: @CommonRawValue Operation<Routing, State>
    ): RoutingElement<Routing, State> =
        RoutingElement(
            key = key,
            fromState = fromState,
            targetState = newTargetState,
            operation = operation,
            transitionHistory =
            if (fromState != newTargetState) {
                transitionHistory + listOf(fromState to newTargetState)
            } else transitionHistory
        )

    fun onTransitionFinished(): RoutingElement<Routing, State> =
        RoutingElement(
            key = key,
            fromState = targetState,
            targetState = targetState,
            operation = operation,
            transitionHistory = emptyList()
        )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoutingElement<*, *>

        if (key != other.key) return false
        if (fromState != other.fromState) return false
        if (targetState != other.targetState) return false
        if (operation != other.operation) return false
        if (transitionHistory != other.transitionHistory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (fromState?.hashCode() ?: 0)
        result = 31 * result + (targetState?.hashCode() ?: 0)
        result = 31 * result + operation.hashCode()
        result = 31 * result + transitionHistory.hashCode()
        return result
    }

    override fun toString(): String {
        return "RoutingElement(key=$key, fromState=$fromState, targetState=$targetState, operation=$operation, transitionHistory=$transitionHistory)"
    }
}
