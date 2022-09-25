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
import com.bumble.appyx.core.navigation.transition.JumpToEndTransitionHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.Active2
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack1
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack.State.StashedInBackStack2
import com.bumble.appyx.navmodel.dualbackstack.operation.pop
import com.bumble.appyx.navmodel.dualbackstack.operation.popLeft
import com.bumble.appyx.navmodel.dualbackstack.operation.popRight
import com.bumble.appyx.navmodel.dualbackstack.operation.pushLeft
import com.bumble.appyx.navmodel.dualbackstack.operation.pushRight
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

class DualBackStackContainerNode(
    buildContext: BuildContext,
    private val showTwoPanelsFlow: StateFlow<Boolean>,
    private val dualBackStack: DualBackStack<NavTarget> = DualBackStack(
        leftElement = NavTarget.LeftScreen(1),
        rightElement = NavTarget.RightScreen(1),
        showTwoPanelsFlow = showTwoPanelsFlow,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<DualBackStackContainerNode.NavTarget>(
    navModel = dualBackStack,
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
            val allChildren by dualBackStack.childrenAsState()
            val visibleChildren by dualBackStack.visibleChildrenAsState()
            val currentTwoPanels by showTwoPanelsFlow.collectAsState()

            val viewState by derivedStateOf {
                getViewState(
                    visibleChildren = visibleChildren,
                    allChildren = allChildren
                )
            }

            Column {
                PanelControls(
                    viewState = viewState,
                    pushLeftClick = {
                        dualBackStack.pushLeft(
                            NavTarget.LeftScreen(viewState.allLeftPanelCount + 1)
                        )
                    },
                    pushRightClick = {
                        dualBackStack.pushRight(
                            NavTarget.RightScreen(viewState.allRightPanelCount + 1)
                        )
                    },
                    popLeftClick = { dualBackStack.popLeft() },
                    popRightClick = { dualBackStack.popRight() },
                    popClick = { dualBackStack.pop() }
                )

                Text("Two panel mode: $currentTwoPanels")

                if (visibleChildren.isNotEmpty()) {
                    Panels(
                        viewState = viewState,
                        visibleChildren = visibleChildren
                    )
                }
            }
        }
    }

    @Composable
    private fun Panels(
        viewState: DualBackStackViewState,
        visibleChildren: List<NavElement<NavTarget, out DualBackStack.State>>
    ) {
        check(viewState.totalVisibleChildren <= MAXIMUM_VISIBLE_CHILDREN) {
            "There should not be more than $MAXIMUM_VISIBLE_CHILDREN elements. " +
                    "elements: ${viewState.totalVisibleChildren}"
        }

        if (viewState.activeRightPanelCount == 0) {
            repeat(viewState.activeLeftPanelCount) { index ->
                AddChild(visibleChildren[index])
            }
        } else if (viewState.activeLeftPanelCount == 0) {
            repeat(viewState.activeRightPanelCount) { index ->
                AddChild(visibleChildren[index])
            }
        } else {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().weight(1f)) {
                    repeat(viewState.activeLeftPanelCount) { index ->
                        AddChild(visibleChildren[index])
                    }
                }
                Box(Modifier.fillMaxSize().weight(1f)) {
                    repeat(viewState.activeRightPanelCount) { index ->
                        AddChild(visibleChildren[viewState.activeLeftPanelCount + index])
                    }
                }
            }
        }
    }

    @Composable
    private fun AddChild(element: NavElement<NavTarget, out DualBackStack.State>) {
        Child(
            navElement = element,
            transitionHandler = JumpToEndTransitionHandler()
        ) { child, _ ->
            child()
        }
    }

    private fun getViewState(
        visibleChildren: List<NavElement<NavTarget, out DualBackStack.State>>,
        allChildren: List<NavElement<NavTarget, out DualBackStack.State>>,
    ): DualBackStackViewState {
        // Refactor to avoid looping through the list so many times.
        val activeLeftPanelCount: Int by derivedStateOf {
            visibleChildren.count { it.targetState == Active1 || it.fromState == Active1 }
        }
        val allLeftPanelCount: Int by derivedStateOf {
            allChildren.count {
                it.targetState == Active1 || it.fromState == Active1 ||
                        it.targetState == StashedInBackStack1 || it.fromState == StashedInBackStack1
            }
        }

        val activeRightPanelCount: Int by derivedStateOf {
            visibleChildren.count { it.targetState == Active2 || it.fromState == Active2 }
        }
        val allRightPanelCount: Int by derivedStateOf {
            allChildren.count {
                it.targetState == Active2 || it.fromState == Active2 ||
                        it.targetState == StashedInBackStack2 || it.fromState == StashedInBackStack2
            }
        }
        return DualBackStackViewState(
            totalVisibleChildren = visibleChildren.size,
            activeLeftPanelCount = activeLeftPanelCount,
            allLeftPanelCount = allLeftPanelCount,
            activeRightPanelCount = activeRightPanelCount,
            allRightPanelCount = allRightPanelCount,
        )
    }

    private companion object {
        // There can are two active, and two that can be animated between.
        private const val MAXIMUM_VISIBLE_CHILDREN = 4
    }
}
