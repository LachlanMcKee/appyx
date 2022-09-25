package com.bumble.appyx.navmodel.dualbackstack.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStackElements
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Operation:
 *
 * TODO
 */
@Parcelize
class Pop<T : Any>(private val twoPanelsEnabled: Boolean) : BackStackOperation<T> {
    @IgnoredOnParcel
    private val popLeft = PopLeft<T>(twoPanelsEnabled)

    @IgnoredOnParcel
    private val popRight = PopRight<T>(twoPanelsEnabled)

    override fun isApplicable(elements: NavElements<T, DualBackStack.State>): Boolean =
        popRight.isApplicable(elements) || popLeft.isApplicable(elements)

    override fun invoke(
        elements: NavElements<T, DualBackStack.State>
    ): NavElements<T, DualBackStack.State> {
        return if (popRight.isApplicable(elements)) {
            popRight.invoke(elements)
        } else {
            popLeft.invoke(elements)
        }
    }
}

/**
 * Operation:
 *
 * TODO
 */
@Parcelize
class PopLeft<T : Any>(private val twoPanelsEnabled: Boolean) : BackStackOperation<T> {

    override fun isApplicable(elements: DualBackStackElements<T>): Boolean {
        val stashedCount = elements.count { it.targetState == StashedInBackStack1 }
        return (elements.any { it.targetState == Active1 } && stashedCount > 0) ||
                stashedCount > 1
    }

    override fun invoke(
        elements: DualBackStackElements<T>
    ): DualBackStackElements<T> {
        val newElementsList = elements.toMutableList()
        val listIterator = newElementsList.listIterator()

        while (listIterator.hasNext()) {
            val index = listIterator.nextIndex()
            val element = listIterator.next()

            if (element.targetState == Active1) {
                listIterator.remove()

                check(index != 0) { "Cannot pop the last active left panel" }

                val stashedPanel1 = listIterator.previous()
                listIterator.set(
                    stashedPanel1.transitionTo(
                        newTargetState = Active1,
                        operation = this
                    )
                )
                break

            } else if (element.targetState == StashedInBackStack2 || element.targetState == Active2) {
                // In this case there was no active panel 1, so we are not in 'two panel' mode.
                check(index > 1) { "Cannot pop the last active left panel" }

                listIterator.previous()
                listIterator.previous()
                listIterator.remove()
                break
            }
        }

        return newElementsList
    }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

@Parcelize
class PopRight<T : Any>(private val twoPanelsEnabled: Boolean) : BackStackOperation<T> {

    override fun isApplicable(elements: DualBackStackElements<T>): Boolean =
        elements.any { it.targetState == Active2 }

    override fun invoke(
        elements: DualBackStackElements<T>
    ): DualBackStackElements<T> {
        check(elements.last().targetState == Active2) {
            "Invoked when no active panel 2 elements"
        }

        val newElements = elements.dropLast(1)
        val newLastElement = newElements.lastOrNull()

        return if (newLastElement != null) {
            when (newLastElement.targetState) {
                StashedInBackStack2 -> {
                    newElements.dropLast(1) + newLastElement.transitionTo(
                        newTargetState = Active2,
                        operation = this
                    )
                }
                StashedInBackStack1 -> {
                    newElements.dropLast(1) + newLastElement.transitionTo(
                        newTargetState = Active1,
                        operation = this
                    )
                }
                Active1 -> {
                    if (!twoPanelsEnabled) {
                        throw IllegalStateException(
                            "Unexpected state when popping right: ${newLastElement.targetState}"
                        )
                    } else {
                        newElements
                    }
                }
                else -> {
                    throw IllegalStateException(
                        "Unexpected state when popping right: ${newLastElement.targetState}"
                    )
                }
            }
        } else {
            newElements
        }
    }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <T : Any> DualBackStack<T>.popLeft() {
    accept(PopLeft(twoPanelsEnabled))
}

fun <T : Any> DualBackStack<T>.popRight() {
    accept(PopRight(twoPanelsEnabled))
}

fun <T : Any> DualBackStack<T>.pop() {
    accept(Pop(twoPanelsEnabled))
}
