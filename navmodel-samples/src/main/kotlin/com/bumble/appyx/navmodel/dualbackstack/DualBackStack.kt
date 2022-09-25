package com.bumble.appyx.navmodel.dualbackstack

import android.os.Parcelable
import com.bumble.appyx.core.navigation.BaseNavModel
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.core.navigation.Operation.Noop
import com.bumble.appyx.core.navigation.onscreen.OnScreenStateResolver
import com.bumble.appyx.core.navigation.operationstrategies.ExecuteImmediately
import com.bumble.appyx.core.navigation.operationstrategies.OperationStrategy
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Destroyed1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Destroyed2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack1
import com.bumble.appyx.navmodel.dualbackstack.backpresshandler.DualBackPressHandlerStrategy
import com.bumble.appyx.navmodel.dualbackstack.backpresshandler.PopBackPressHandler
import com.bumble.appyx.navmodel.dualbackstack.operation.panelModeChanged
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class DualBackStack<NavTarget : Any>(
    leftElement: NavTarget,
    rightElement: NavTarget? = null,
    savedStateMap: SavedStateMap?,
    private val showTwoPanelsFlow: StateFlow<Boolean>,
    key: String = KEY_NAV_MODEL,
    backPressHandler: DualBackPressHandlerStrategy<NavTarget> = PopBackPressHandler(),
    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),
    screenResolver: OnScreenStateResolver<State> = DualBackStackOnScreenResolver
) : BaseNavModel<NavTarget, State>(
    backPressHandler = backPressHandler,
    screenResolver = screenResolver,
    operationStrategy = operationStrategy,
    finalStates = setOf(Destroyed1, Destroyed2),
    savedStateMap = savedStateMap,
    key = key,
) {
    val twoPanelsEnabled: Boolean
        get() = showTwoPanelsFlow.value

    init {
        backPressHandler.setTwoPanelsEnabledFunction { twoPanelsEnabled }

        scope.launch {
            var initialPanelModeSet = false
            showTwoPanelsFlow.collectLatest { twoPanelsEnabled ->
                if (initialPanelModeSet) {
                    panelModeChanged(twoPanelsEnabled)
                }
                initialPanelModeSet = true
            }
        }
    }

    sealed class State(val panel: Panel) : Parcelable {
        @Parcelize
        object Created1 : State(Panel.PANEL_1)

        @Parcelize
        object Created2 : State(Panel.PANEL_2)

        @Parcelize
        object Active1 : State(Panel.PANEL_1)

        @Parcelize
        object Active2 : State(Panel.PANEL_2)

        @Parcelize
        object StashedInBackStack1 : State(Panel.PANEL_1)

        @Parcelize
        object StashedInBackStack2 : State(Panel.PANEL_2)

        @Parcelize
        object Destroyed1 : State(Panel.PANEL_1)

        @Parcelize
        object Destroyed2 : State(Panel.PANEL_2)
    }

    enum class Panel {
        PANEL_1, PANEL_2
    }

    override val initialElements by lazy {
        listOfNotNull(
            DualBackStackElement(
                key = NavKey(leftElement),
                fromState = if (rightElement == null || twoPanelsEnabled) Active1 else StashedInBackStack1,
                targetState = if (rightElement == null || twoPanelsEnabled) Active1 else StashedInBackStack1,
                operation = Noop()
            ),
            rightElement?.let { element ->
                DualBackStackElement(
                    key = NavKey(element),
                    fromState = Active2,
                    targetState = Active2,
                    operation = Noop()
                )
            }
        )
    }

}
