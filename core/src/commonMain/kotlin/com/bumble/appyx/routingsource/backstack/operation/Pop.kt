package com.bumble.appyx.routingsource.backstack.operation

import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.BackStackElements
import com.bumble.appyx.routingsource.backstack.activeIndex
import kotlinx.parcelize.Parcelize

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<T : Any> : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        elements.any { it.targetState == BackStack.TransitionState.ACTIVE } &&
                elements.any { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }

    override fun invoke(
        elements: BackStackElements<T>
    ): BackStackElements<T> {

        val destroyIndex = elements.activeIndex
        val unStashIndex =
            elements.indexOfLast { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }
        require(destroyIndex != -1) { "Nothing to destroy, state=$elements" }
        require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }
        return elements.mapIndexed { index, element ->
            when (index) {
                destroyIndex -> element.transitionTo(
                    newTargetState = BackStack.TransitionState.DESTROYED,
                    operation = this
                )
                unStashIndex -> element.transitionTo(
                    newTargetState = BackStack.TransitionState.ACTIVE,
                    operation = this
                )
                else -> element
            }
        }
    }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <T : Any> BackStack<T>.pop() {
    accept(Pop())
}
