package com.bumble.appyx.sample.dualbackstack

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.core.composable.Child
import com.bumble.appyx.core.composable.childrenAsState
import com.bumble.appyx.core.composable.visibleChildrenAsState
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.NavElement
import com.bumble.appyx.core.navigation.model.combined.plus
import com.bumble.appyx.core.navigation.transition.JumpToEndTransitionHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

class TwoBackStacksContainerNode(
    buildContext: BuildContext,
    private val showTwoPanelsFlow: StateFlow<Boolean>,
    private val backStack1: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.LeftScreen(1),
        savedStateMap = buildContext.savedStateMap,
        key = "BackStack1",
    ),
    private val backStack2: BackStack<NavTarget> = BackStack(
        initialElement = null,
        savedStateMap = buildContext.savedStateMap,
        key = "BackStack2",
        emptyBackStackAllowed = true
    )
) : ParentNode<TwoBackStacksContainerNode.NavTarget>(
    navModel = backStack1 + backStack2,
    buildContext = buildContext,
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        data class LeftScreen(val count: Int) : NavTarget()

        @Parcelize
        data class RightScreen(val count: Int) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.LeftScreen -> node(buildContext) {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.LightGray),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Left screen: ${navTarget.count}")
                }
            }
            is NavTarget.RightScreen -> node(buildContext) {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.Red),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Right screen: ${navTarget.count}")
                }
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            val visibleBackStack1Children by backStack1.visibleChildrenAsState()
            val visibleBackStack2Children by backStack2.visibleChildrenAsState()

            val allBackStack1Children by backStack1.childrenAsState()
            val allBackStack2Children by backStack2.childrenAsState()
            val currentTwoPanels by showTwoPanelsFlow.collectAsState()

            val viewState by derivedStateOf {
                getViewState(
                    visibleBackStack1Children = visibleBackStack1Children,
                    visibleBackStack2Children = visibleBackStack2Children,
                    allBackStack1Children = allBackStack1Children,
                    allBackStack2Children = allBackStack2Children
                )
            }

            Column {
                PanelControls(
                    viewState = viewState,
                    pushLeftClick = {
                        backStack1.push(NavTarget.LeftScreen(viewState.allLeftPanelCount + 1))
                    },
                    pushRightClick = {
                        backStack2.push(NavTarget.RightScreen(viewState.allRightPanelCount + 1))
                    },
                    popLeftClick = { backStack1.pop() },
                    popRightClick = { backStack2.pop() },
                    popClick = {
                        if (viewState.allRightPanelCount > 0) {
                            backStack2.pop()
                        } else {
                            backStack1.pop()
                        }
                    }
                )

                Text("Two panel mode: $currentTwoPanels")

                if (viewState.activeLeftPanelCount > 0 || viewState.activeRightPanelCount > 0) {
                    Panels(
                        viewState = viewState,
                        visibleBackStack1Children = visibleBackStack1Children,
                        visibleBackStack2Children = visibleBackStack2Children
                    )
                }
            }
        }
    }

    @Composable
    private fun Panels(
        viewState: DualBackStackViewState,
        visibleBackStack1Children: List<NavElement<NavTarget, out BackStack.State>>,
        visibleBackStack2Children: List<NavElement<NavTarget, out BackStack.State>>
    ) {
        if (viewState.activeRightPanelCount == 0) {
            repeat(viewState.activeLeftPanelCount) { index ->
                AddChild(visibleBackStack1Children[index])
            }
        } else if (viewState.activeLeftPanelCount == 0) {
            repeat(viewState.activeRightPanelCount) { index ->
                AddChild(visibleBackStack2Children[index])
            }
        } else {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().weight(1f)) {
                    repeat(visibleBackStack1Children.size) { index ->
                        AddChild(visibleBackStack1Children[index])
                    }
                }
                Box(Modifier.fillMaxSize().weight(1f)) {
                    repeat(visibleBackStack2Children.size) { index ->
                        AddChild(visibleBackStack2Children[index])
                    }
                }
            }
        }
    }

    @Composable
    private fun AddChild(element: NavElement<NavTarget, out BackStack.State>) {
        Child(
            navElement = element,
            transitionHandler = JumpToEndTransitionHandler()
        ) { child, _ ->
            child()
        }
    }

    private fun getViewState(
        visibleBackStack1Children: List<NavElement<NavTarget, out BackStack.State>>,
        visibleBackStack2Children: List<NavElement<NavTarget, out BackStack.State>>,
        allBackStack1Children: List<NavElement<NavTarget, out BackStack.State>>,
        allBackStack2Children: List<NavElement<NavTarget, out BackStack.State>>,
    ): DualBackStackViewState =
        DualBackStackViewState(
            totalVisibleChildren = visibleBackStack1Children.size + visibleBackStack2Children.size,
            activeLeftPanelCount = visibleBackStack1Children.size,
            allLeftPanelCount = allBackStack1Children.size,
            activeRightPanelCount = visibleBackStack2Children.size,
            allRightPanelCount = allBackStack2Children.size,
        )
}
