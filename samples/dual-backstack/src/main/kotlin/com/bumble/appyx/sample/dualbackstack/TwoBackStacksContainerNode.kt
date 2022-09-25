package com.bumble.appyx.sample.dualbackstack

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

//            val visibleChildren by dualBackStack.visibleChildrenAsState()
//            LaunchedEffect(dualBackStack) {
//                dualBackStack.init(twoPanelFlow)
//            }
            val currentTwoPanels by showTwoPanelsFlow.collectAsState()
//
//            // Refactor to avoid looping through the list so many times.
//            val activeLeftPanelCount: Int =
//                visibleChildren.count { it.targetState == Active1 || it.fromState == Active1 }
//            val allLeftPanelCount: Int =
//                allChildren.count {
//                    it.targetState == Active1 || it.fromState == Active1 ||
//                            it.targetState == StashedInBackStack1 || it.fromState == StashedInBackStack1
//                }
//
//            val activeRightPanelCount: Int =
//                visibleChildren.count { it.targetState == Active2 || it.fromState == Active2 }
//            val allRightPanelCount: Int =
//                allChildren.count {
//                    it.targetState == Active2 || it.fromState == Active2 ||
//                            it.targetState == StashedInBackStack2 || it.fromState == StashedInBackStack2
//                }

            val backStack1VisibleChildrenCount = visibleBackStack1Children.count()
            val backStack2VisibleChildrenCount = visibleBackStack2Children.count()
            val backStack1AllChildrenCount = allBackStack1Children.count()
            val backStack2AllChildrenCount = allBackStack2Children.count()

            Column {
                Row {
                    Button(onClick = {
                        backStack1.push(
                            NavTarget.LeftScreen(
                                backStack1AllChildrenCount + 1
                            )
                        )
                    }) {
                        Text("Add Panel 1")
                    }
                    Button(onClick = {
                        backStack2.push(
                            NavTarget.RightScreen(
                                backStack2AllChildrenCount + 1
                            )
                        )
                    }) {
                        Text("Add Panel 2")
                    }
                }
                Row {
                    Button(
                        onClick = { backStack1.pop() },
                        enabled = backStack1AllChildrenCount > 1
                    ) {
                        Text("Pop Panel 1")
                    }
                    Button(
                        onClick = { backStack2.pop() },
                        enabled = backStack2AllChildrenCount > 0
                    ) {
                        Text("Pop Panel 2")
                    }
                    Button(
                        onClick = {
                            if (backStack2AllChildrenCount > 0) {
                                backStack2.pop()
                            } else {
                                backStack1.pop()
                            }
                        },
                        enabled = backStack1AllChildrenCount > 1 || backStack2AllChildrenCount > 0
                    ) {
                        Text("Pop")
                    }
                }
                Text("Two panel mode: $currentTwoPanels")

                if (backStack1VisibleChildrenCount > 0 || backStack2VisibleChildrenCount > 0) {
                    if (backStack2VisibleChildrenCount == 0) {
                        repeat(backStack1VisibleChildrenCount) { index ->
                            AddChild(visibleBackStack1Children[index])
                        }
                    } else if (backStack1VisibleChildrenCount == 0) {
                        repeat(backStack2VisibleChildrenCount) { index ->
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
}
