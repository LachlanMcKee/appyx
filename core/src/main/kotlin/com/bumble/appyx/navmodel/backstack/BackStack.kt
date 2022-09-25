package com.bumble.appyx.navmodel.backstack

import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BackPressHandlerStrategy
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.navmodel.backstack.BackStack.State
import com.bumble.appyx.navmodel.backstack.BackStack.State.DESTROYED
import com.bumble.appyx.navmodel.backstack.backpresshandler.PopBackPressHandler
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.backstack.BackStack.State.ACTIVE

class BackStack<NavTarget : Any>(
    initialElement: NavTarget?,
    savedStateMap: SavedStateMap?,
    key: String = KEY_NAV_MODEL,
    val emptyBackStackAllowed: Boolean = false,
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = PopBackPressHandler(
        emptyBackStackAllowed
    ),
    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<State> = BackStackOnScreenResolver
) : BaseNavModel<NavTarget, State>(
    backPressHandler = backPressHandler,
    screenResolver = screenResolver,
    operationStrategy = operationStrategy,
    finalState = DESTROYED,
    savedStateMap = savedStateMap,
    key = key,
) {
    init {
        check(initialElement != null || emptyBackStackAllowed) {
            "No initial element and emptyBackStackAllowed=false"
        }
    }

    enum class State {
        CREATED, ACTIVE, STASHED, DESTROYED,
    }

    override val initialElements = listOfNotNull(
        initialElement?.let { element ->
            BackStackElement(
                key = NavKey(element),
                fromState = ACTIVE,
                targetState = ACTIVE,
                operation = Noop()
            )
        }
    )

}
