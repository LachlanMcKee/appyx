package com.bumble.appyx.navmodel.dualbackstack.operation

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStackElements
import kotlinx.parcelize.Parcelize

/**
 * Operation:
 *
 * TODO
 */
@Parcelize
internal data class PanelModeChanged<T : Any>(
    private val twoPanelsEnabled: Boolean
) : BackStackOperation<T> {

    override fun isApplicable(elements: DualBackStackElements<T>): Boolean = true

    override fun invoke(
        elements: DualBackStackElements<T>,
    ): DualBackStackElements<T> {
        val newElementsList = elements.toMutableList()
        val listIterator = newElementsList.listIterator()

        var previousElement: NavElement<T, DualBackStack.State>? =
            null

        while (listIterator.hasNext()) {
            val index = listIterator.nextIndex()
            val element = listIterator.next()
            val isLast = index == newElementsList.lastIndex

            if (!twoPanelsEnabled) {
                if (previousElement != null && previousElement.eitherState(Active1) &&
                    (element.eitherState(StashedInBackStack2) || element.eitherState(Active2))
                ) {
                    // We must call previous twice to actually go back to the previous element.
                    listIterator.previous()
                    val stashedBackStackElement = listIterator.previous()
                    listIterator.set(
                        stashedBackStackElement.transitionTo(
                            newTargetState = StashedInBackStack1,
                            operation = this
                        )
                    )
                    break
                } else if (isLast && element.eitherState(StashedInBackStack1)) {
                    listIterator.set(
                        element.transitionTo(
                            newTargetState = Active1,
                            operation = this
                        )
                    )
                    break
                }
            }
            if (twoPanelsEnabled &&
                (element.eitherState(StashedInBackStack2) || element.eitherState(Active2)) &&
                previousElement != null && previousElement.eitherState(StashedInBackStack1)
            ) {
                // We must call previous twice to actually go back to the previous element.
                listIterator.previous()
                val stashedBackStackElement = listIterator.previous()
                listIterator.set(
                    stashedBackStackElement.transitionTo(
                        newTargetState = Active1,
                        operation = this
                    )
                )
                break
            }
            previousElement = element
        }
        return newElementsList
    }
}

internal fun <T : Any> DualBackStack<T>.panelModeChanged(twoPanelsEnabled: Boolean) {
    accept(PanelModeChanged(twoPanelsEnabled))
}
