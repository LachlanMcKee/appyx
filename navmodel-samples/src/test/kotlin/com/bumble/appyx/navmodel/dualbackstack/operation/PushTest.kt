package com.bumble.appyx.navmodel.dualbackstack.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Created1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Created2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack2
import com.bumble.appyx.navmodel.dualbackstack.RoutingElementComparison
import com.bumble.appyx.navmodel.dualbackstack.assertStates
import com.bumble.appyx.navmodel.dualbackstack.createBackStack
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.Test

class PushTest {

    @Test
    fun `GIVEN only left initial state AND not two panels THEN verify states`() {
        val dualBackStack =
            createBackStack(left = "left1", right = null, twoPanelsFlow = MutableStateFlow(false))

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
    fun `GIVEN both initial states AND not two panels THEN verify states`() {
        val dualBackStack =
            createBackStack(
                left = "left1",
                right = "right1",
                twoPanelsFlow = MutableStateFlow(false)
            )

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = StashedInBackStack1,
                targetState = StashedInBackStack1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
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
    fun `GIVEN only left initial state AND two panels THEN verify states`() {
        val dualBackStack =
            createBackStack(left = "left1", right = null, twoPanelsFlow = MutableStateFlow(true))

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
    fun `GIVEN both initial states AND two panels THEN verify states`() {
        val dualBackStack =
            createBackStack(
                left = "left1",
                right = "right1",
                twoPanelsFlow = MutableStateFlow(true)
            )

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = Active1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
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
    fun `GIVEN only left initial state AND not two panels WHEN push left THEN verify states`() {
        val dualBackStack =
            createBackStack(left = "left1", right = null, twoPanelsFlow = MutableStateFlow(false))

        dualBackStack.pushLeft("left2")

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
                targetState = Active1,
                operationType = Push::class.java,
                transitionHistory = listOf(Created1 to Active1)
            )
        )
    }

    @Test
    fun `GIVEN only left initial state AND not two panels WHEN push right THEN verify states`() {
        val dualBackStack =
            createBackStack(left = "left1", right = null, twoPanelsFlow = MutableStateFlow(false))

        dualBackStack.pushRight("right1")

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = StashedInBackStack1,
                operationType = Push::class.java,
                transitionHistory = listOf(Active1 to StashedInBackStack1)
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }

    @Test
    fun `GIVEN both initial states AND not two panels WHEN push right THEN verify states`() {
        val dualBackStack =
            createBackStack(
                left = "left1",
                right = "right1",
                twoPanelsFlow = MutableStateFlow(false)
            )

        dualBackStack.pushRight("right2")

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = StashedInBackStack1,
                targetState = StashedInBackStack1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Active2,
                targetState = StashedInBackStack2,
                operationType = Push::class.java,
                transitionHistory = listOf(Active2 to StashedInBackStack2)
            ),
            RoutingElementComparison(
                keyNavTarget = "right2",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }

    @Test
    fun `GIVEN only left initial state AND two panels WHEN push right THEN verify states`() {
        val dualBackStack =
            createBackStack(left = "left1", right = null, twoPanelsFlow = MutableStateFlow(true))

        dualBackStack.pushRight("right1")

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = Active1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }

    @Test
    fun `GIVEN both initial states AND two panels WHEN push right THEN verify states`() {
        val dualBackStack =
            createBackStack(
                left = "left1",
                right = "right1",
                twoPanelsFlow = MutableStateFlow(true)
            )

        dualBackStack.pushRight("right1")

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = Active1,
                targetState = Active1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Active2,
                targetState = StashedInBackStack2,
                operationType = Push::class.java,
                transitionHistory = listOf(Active2 to StashedInBackStack2)
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }

    @Test
    fun `GIVEN only left initial state AND not two panels WHEN push both THEN verify states`() {
        val dualBackStack =
            createBackStack(left = "left1", right = null, twoPanelsFlow = MutableStateFlow(false))

        dualBackStack.pushBoth("left2", "right1")

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
                fromState = StashedInBackStack1,
                targetState = StashedInBackStack1,
                operationType = Push::class.java,
                transitionHistory = emptyList()
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }

    @Test
    fun `GIVEN both initial states AND not two panels WHEN push both THEN verify states`() {
        val dualBackStack =
            createBackStack(
                left = "left1",
                right = "right1",
                twoPanelsFlow = MutableStateFlow(false)
            )

        dualBackStack.pushBoth("left2", "right2")

        dualBackStack.assertStates(
            RoutingElementComparison(
                keyNavTarget = "left1",
                fromState = StashedInBackStack1,
                targetState = StashedInBackStack1,
                operationType = Operation.Noop::class.java,
                transitionHistory = emptyList()
            ),
            RoutingElementComparison(
                keyNavTarget = "left2",
                fromState = StashedInBackStack1,
                targetState = StashedInBackStack1,
                operationType = Push::class.java,
                transitionHistory = emptyList()
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Active2,
                targetState = StashedInBackStack2,
                operationType = Push::class.java,
                transitionHistory = listOf(Active2 to StashedInBackStack2)
            ),
            RoutingElementComparison(
                keyNavTarget = "right2",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }

    @Test
    fun `GIVEN only left initial state AND two panels WHEN push both THEN verify states`() {
        val dualBackStack =
            createBackStack(left = "left1", right = null, twoPanelsFlow = MutableStateFlow(true))

        dualBackStack.pushBoth("left2", "right1")

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
                targetState = Active1,
                operationType = Push::class.java,
                transitionHistory = listOf(Created1 to Active1)
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }

    @Test
    fun `GIVEN both initial states AND two panels WHEN push both THEN verify states`() {
        val dualBackStack =
            createBackStack(
                left = "left1",
                right = "right1",
                twoPanelsFlow = MutableStateFlow(true)
            )

        dualBackStack.pushBoth("left2", "right2")

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
                targetState = Active1,
                operationType = Push::class.java,
                transitionHistory = listOf(Created1 to Active1)
            ),
            RoutingElementComparison(
                keyNavTarget = "right1",
                fromState = Active2,
                targetState = StashedInBackStack2,
                operationType = Push::class.java,
                transitionHistory = listOf(Active2 to StashedInBackStack2)
            ),
            RoutingElementComparison(
                keyNavTarget = "right2",
                fromState = Created2,
                targetState = Active2,
                operationType = Push::class.java,
                transitionHistory = listOf(Created2 to Active2)
            )
        )
    }
}
