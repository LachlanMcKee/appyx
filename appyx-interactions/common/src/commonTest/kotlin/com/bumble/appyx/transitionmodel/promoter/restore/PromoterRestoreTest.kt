package com.bumble.appyx.transitionmodel.promoter.restore

import com.bumble.appyx.InteractionTarget
import com.bumble.appyx.interactions.core.state.MutableSavedStateMapImpl
import com.bumble.appyx.transitionmodel.promoter.PromoterModel
import com.bumble.appyx.transitionmodel.promoter.operation.AddFirst
import kotlin.test.Test
import kotlin.test.assertEquals

class PromoterRestoreTest {

    @Test
    fun GIVEN_promoter_with_2_children_WHEN_state_restored_THEN_second_child_first() {
        val savedStateMap = mutableMapOf<String, Any?>()
        val promoter = PromoterModel<InteractionTarget>(
            savedStateMap = savedStateMap
        )

        promoter.operation(AddFirst(InteractionTarget.Child1))
        promoter.operation(AddFirst(InteractionTarget.Child2))

        val state = MutableSavedStateMapImpl(savedStateMap) { true }
        promoter.saveInstanceState(state)

        val newPromoter = PromoterModel<InteractionTarget>(
            savedStateMap = state.savedState
        )

        assertEquals(
            promoter.output.value.currentTargetState,
            newPromoter.output.value.currentTargetState
        )
    }

}
