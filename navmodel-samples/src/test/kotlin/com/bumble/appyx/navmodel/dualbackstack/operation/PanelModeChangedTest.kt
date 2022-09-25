package com.bumble.appyx.navmodel.dualbackstack.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Created1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack1
import com.bumble.appyx.navmodel.dualbackstack.RoutingElementComparison
import com.bumble.appyx.navmodel.dualbackstack.assertStates
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.Test

class PanelModeChangedTest {
    private lateinit var twoPanelsFlow: MutableStateFlow<Boolean>
    private lateinit var dualBackStack: DualBackStack<String>

    @Test
    fun `GIVEN only left initial state AND not two panels WHEN two panels enabled THEN verify states`() {
        createBackStack(left = "left1", right = null, initialTwoPanelsEnabled = false)

        updateTwoPanelsEnabled(true)

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = Active1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            )
        )
    }

    @Test
    fun `GIVEN only left initial state AND two panels WHEN two panels disabled THEN verify states`() {
        createBackStack(left = "left1", right = null, initialTwoPanelsEnabled = true)

        updateTwoPanelsEnabled(false)

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = Active1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            )
        )
    }

    @Test
    fun `GIVEN both initial states AND not two panels WHEN two panels enabled THEN verify states`() {
        createBackStack(left = "left1", right = "right1", initialTwoPanelsEnabled = false)

        updateTwoPanelsEnabled(true)

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = StashedInBackStack1,
                targetState = Active1,
                operationType = PanelModeChanged::class.java,
                transitionHistory = listOf(StashedInBackStack1 to Active1)
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Active2,
                targetState = Active2,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            )
        )
    }

    @Test
    fun `GIVEN both initial states AND two panels WHEN two panels disabled THEN verify states`() {
        createBackStack(left = "left1", right = "right1", initialTwoPanelsEnabled = true)

        updateTwoPanelsEnabled(false)

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = StashedInBackStack1,
                operationType = PanelModeChanged::class.java,
                transitionHistory = listOf(Active1 to StashedInBackStack1)
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Active2,
                targetState = Active2,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            )
        )
    }

    @Test
    fun `GIVEN both initial states AND two panels AND pushed left WHEN two panels disabled THEN verify states`() {
        createBackStack(left = "left1", right = "right1", initialTwoPanelsEnabled = true)
        dualBackStack.pushLeft("left2")

        updateTwoPanelsEnabled(false)

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = StashedInBackStack1,
                operationType = Push::class.java,
                transitionHistory = listOf(Active1 to StashedInBackStack1)
            ),
            RoutingElementComparison(
                keyNavTarget = "left2",
                fromState = Created1,
                targetState = StashedInBackStack1,
                operationType = PanelModeChanged::class.java,
                transitionHistory = listOf(Created1 to Active1, Created1 to StashedInBackStack1)
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Active2,
                targetState = Active2,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            )
        )
    }

//    @Test
//    fun `foo`() {
//        createBackStack(left = "left1", right = "right1", initialTwoPanelsEnabled = true)
//        dualBackStack.pushLeft("left2")
//        dualBackStack.onTransitionFinished(dualBackStack.elements.value.mapNotNull { if (it.isTransitioning) it.key else null })
//
//        updateTwoPanelsEnabled(false)
//        dualBackStack.onTransitionFinished(dualBackStack.elements.value.mapNotNull { if (it.isTransitioning) it.key else null })
//        updateTwoPanelsEnabled(true)
//
//    YOU NEED TO CALL onTransitionFinished IN ALL THE OTHER TESTS. THIS MEANS THE HISTORY COULD BE TOTALLY WRONG!!!
//
//        dualBackStack.assertStates(
//            RoutingElementComparison(
//                keyRouting = "left1",
//                fromState = StashedInBackStack1,
//                targetState = StashedInBackStack1,
//                operationType = Push::class.java,
//                transitionHistory = emptyList()
//            ),
//            RoutingElementComparison(
//                keyRouting = "left2",
//                fromState = StashedInBackStack1,
//                targetState = Active1,
//                operationType = PanelModeChanged::class.java,
//                transitionHistory = listOf(StashedInBackStack1 to Active1)
//            ),
//            RoutingElementComparison(
//                keyRouting = "right1",
//                fromState = Active2,
//                targetState = Active2,
//                operationType = Operation.Noop::class.java,
//                transitionHistory = emptyList()
//            )
//        )
//    }

    private fun createBackStack(
        left: String,
        right: String?,
        initialTwoPanelsEnabled: Boolean
    ) {
        twoPanelsFlow = MutableStateFlow(initialTwoPanelsEnabled)
        dualBackStack = DualBackStack(
            leftElement = left,
            rightElement = right,
            savedStateMap = null,
            showTwoPanelsFlow = twoPanelsFlow
        )
    }

    private fun updateTwoPanelsEnabled(enabled: Boolean) {
        // Ensure the state is accessed before changing the flow, otherwise it's not initalised.
        dualBackStack.screenState
        twoPanelsFlow.value = enabled
    }
}
