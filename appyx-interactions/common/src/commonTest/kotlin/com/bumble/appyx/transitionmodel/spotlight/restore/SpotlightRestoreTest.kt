package com.bumble.appyx.transitionmodel.spotlight.restore

import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.InteractionTarget.Child1
import com.bumble.appyx.InteractionTarget.Child2
import com.bumble.appyx.InteractionTarget.Child3
import com.bumble.appyx.interactions.core.state.MutableSavedStateMapImpl
import com.bumble.appyx.transitionmodel.spotlight.operation.Next
import kotlin.test.Test
import kotlin.test.assertEquals

class SpotlightRestoreTest {

    @Test
    fun GIVEN_spotlight_with_2_elements_WHEN_state_restored_THEN_all_elements_are_retained() {
        val savedStateMap = mutableMapOf<String, Any?>()
        val spotlight = SpotlightModel(
            items = listOf(Child1, Child2),
            savedStateMap = savedStateMap
        )

        spotlight.operation(Next())

        val state = MutableSavedStateMapImpl(savedStateMap) { true }
        spotlight.saveInstanceState(state)

        val newSpotlight = SpotlightModel(
            items = listOf(Child1, Child2),
            savedStateMap = state.savedState
        )

        assertEquals(
            spotlight.output.value.currentTargetState,
            newSpotlight.output.value.currentTargetState
        )
    }
}
