package com.bumble.appyx.navmodel.dualbackstack

import com.bumble.appyx.core.navigation.Operation
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.Assertions.assertEquals

fun createBackStack(
    left: String,
    right: String?,
    twoPanelsFlow: MutableStateFlow<Boolean>
): DualBackStack<String> =
    DualBackStack(
        leftElement = left,
        rightElement = right,
        savedStateMap = null,
        showTwoPanelsFlow = twoPanelsFlow
    )

fun <T : Any> DualBackStack<T>.assertStates(vararg expectedList: RoutingElementComparison) {
    assertEquals(expectedList.size, elements.value.size)

    expectedList.forEachIndexed { index, expected ->
        val actual = elements.value[index]

        assertEquals(expected.keyNavTarget, actual.key.navTarget)
        assertEquals(expected.fromState, actual.fromState)
        assertEquals(expected.targetState, actual.targetState)
        assertEquals(expected.operationType, actual.operation.javaClass)
        assertEquals(expected.transitionHistory, actual.transitionHistory)
    }
}

data class RoutingElementComparison(
    val keyNavTarget: String,
    val fromState: DualBackStack.State,
    val targetState: DualBackStack.State,
    val operationType: Class<out Operation<*, *>>,
    val transitionHistory: List<Pair<DualBackStack.State, DualBackStack.State>>
)
