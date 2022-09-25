package com.bumble.appyx.sample.dualbackstack

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.transition.rememberCombinedHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.replace
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.sample.dualbackstack.BackStackExamplesNode.NavTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

class DualBackStackActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val showTwoPanelsFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
            Column {
                Button(onClick = { showTwoPanelsFlow.value = !showTwoPanelsFlow.value }) {
                    Text("Toggle panels")
                }
                NodeHost(integrationPoint = integrationPoint) {
                    BackStackExamplesNode(
                        buildContext = it,
                        showTwoPanelsFlow = showTwoPanelsFlow
                    )
                }
            }
        }
    }
}

class BackStackExamplesNode(
    buildContext: BuildContext,
    private val showTwoPanelsFlow: StateFlow<Boolean>,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.DualBackStackExample,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<NavTarget>(
    navModel = backStack,
    buildContext = buildContext,
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object DualBackStackExample : NavTarget()

        @Parcelize
        object TwoBackStacksExample : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.DualBackStackExample -> DualBackStackContainerNode(
                buildContext,
                showTwoPanelsFlow
            )
            is NavTarget.TwoBackStacksExample -> TwoBackStacksContainerNode(
                buildContext,
                showTwoPanelsFlow
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = modifier.fillMaxSize()) {
            Row {
                Button(onClick = { backStack.replace(NavTarget.DualBackStackExample) }) {
                    Text("Dual Back stack")
                }
                Button(onClick = { backStack.replace(NavTarget.TwoBackStacksExample) }) {
                    Text("Two Back stacks")
                }
            }
            Children(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .requiredHeight(250.dp),
                navModel = backStack,
                transitionHandler = rememberCombinedHandler(
                    handlers = listOf(rememberBackstackSlider(), rememberBackstackFader())
                )
            )
        }
    }
}
