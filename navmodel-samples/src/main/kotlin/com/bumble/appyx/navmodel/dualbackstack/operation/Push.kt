package com.bumble.appyx.navmodel.dualbackstack.operation

import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.Panel.PANEL_1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.Panel.PANEL_2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Created1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Created2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStackElement
import com.bumble.appyx.navmodel.dualbackstack.DualBackStackElements
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Operation:
 *
 * TODO
 */
@Parcelize
data class Push<T : Any>(
    private val leftElement: @RawValue T?,
    private val rightElement: @RawValue T?,
    private val twoPanelsEnabled: Boolean
) : BackStackOperation<T> {

    init {
        check(leftElement != null || rightElement != null) { "At least one element must be provided" }
    }

    override fun isApplicable(elements: DualBackStackElements<T>): Boolean = true

    override fun invoke(elements: DualBackStackElements<T>): DualBackStackElements<T> {
        val mutableElements = elements.toMutableList()
        if (rightElement != null) {
            pushRight(mutableElements, rightElement)
        }
        if (leftElement != null) {
            pushLeft(mutableElements, leftElement)
        }
        return mutableElements
    }

    private fun pushLeft(
        elements: MutableList<NavElement<T, DualBackStack.State>>,
        leftElement: @RawValue T
    ) {
        val listIterator = elements.listIterator()
        while (listIterator.hasNext()) {
            val index = listIterator.nextIndex()
            val element = listIterator.next()
            val isLastElement = index == elements.lastIndex

            // Search until we find a PANEL_2 element.
            if (element.targetState.panel == PANEL_2) {
                // Need to do twice to go back.
                listIterator.previous()
                val previousPanel1Element = listIterator.previous()
                if (previousPanel1Element.targetState == Active1) {
                    listIterator.set(
                        previousPanel1Element.transitionTo(
                            newTargetState = StashedInBackStack1,
                            operation = this
                        )
                    )
                }
                // Move ahead so that this is added after the element that may have been modified.
                listIterator.next()
                listIterator.add(
                    DualBackStackElement(
                        key = NavKey(leftElement),
                        fromState = Created1,
                        // As there are PANEL_2 elements, if we can only show one panel, this is
                        // added to the backstack
                        targetState = if (twoPanelsEnabled) Active1 else StashedInBackStack1,
                        operation = this
                    )
                )
                break
            }

            // If we didn't find a PANEL_2 element, we are at the end of PANEL_1 elements.
            if (isLastElement) {
                listIterator.set(
                    element.transitionTo(
                        newTargetState = StashedInBackStack1,
                        operation = this
                    )
                )
                listIterator.add(
                    DualBackStackElement(
                        key = NavKey(leftElement),
                        fromState = Created1,
                        // As there are no PANEL_2 elements, even if twoPanelsEnabled=false,
                        // this should still be shown.
                        targetState = Active1,
                        operation = this
                    )
                )
            }
        }
    }

    private fun pushRight(
        elements: MutableList<NavElement<T, DualBackStack.State>>,
        rightElement: @RawValue T
    ) {
        val lastElement = elements.lastOrNull()
        if (lastElement != null) {
            when (lastElement.targetState.panel) {
                PANEL_1 -> {
                    // If only one panel can be visible, hide the PANEL_1 element
                    if (!twoPanelsEnabled && lastElement.targetState == Active1) {
                        elements[elements.lastIndex] = lastElement.transitionTo(
                            newTargetState = StashedInBackStack1,
                            operation = this
                        )
                    }
                }
                PANEL_2 -> {
                    elements[elements.lastIndex] = lastElement.transitionTo(
                        newTargetState = StashedInBackStack2,
                        operation = this
                    )
                }
            }
        }
        elements.add(
            DualBackStackElement(
                key = NavKey(rightElement),
                fromState = Created2,
                targetState = Active2,
                operation = this
            )
        )
    }
}

fun <T : Any> DualBackStack<T>.pushLeft(element: T) {
    accept(
        Push(
            leftElement = element,
            rightElement = null,
            twoPanelsEnabled = twoPanelsEnabled
        )
    )
}

fun <T : Any> DualBackStack<T>.pushRight(element: T) {
    accept(
        Push(
            leftElement = null,
            rightElement = element,
            twoPanelsEnabled = twoPanelsEnabled
        )
    )
}

fun <T : Any> DualBackStack<T>.pushBoth(leftElement: T, rightElement: T) {
    accept(
        Push(
            leftElement = leftElement,
            rightElement = rightElement,
            twoPanelsEnabled = twoPanelsEnabled
        )
    )
}
