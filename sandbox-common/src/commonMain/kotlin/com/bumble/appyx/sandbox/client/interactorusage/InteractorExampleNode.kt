package com.bumble.appyx.sandbox.client.interactorusage

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.clienthelper.interactor.Interactor
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.plugin.UpNavigationHandler
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.operation.pop
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.core.routing.transition.rememberCombinedHandler
import com.bumble.appyx.sandbox.client.interactorusage.InteractorExampleNode.Routing
import com.bumble.appyx.sandbox.client.interactorusage.InteractorExampleNode.Routing.Child1
import kotlinx.parcelize.Parcelize

class InteractorExampleNode(
    interactor: Interactor<InteractorExampleNode>,
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Child1,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = backStack,
    buildContext = buildContext,
    plugins = listOf(interactor)
), UpNavigationHandler {

    var child2InfoState by mutableStateOf("Here will appear child2 info")
    var child2And3InfoState by mutableStateOf("Here will appear child2 and child3 combined info")

    sealed class Routing : Parcelable {
        @Parcelize
        object Child1 : Routing()

        @Parcelize
        object Child2 : Routing()

        @Parcelize
        object Child3 : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Child1 -> node(buildContext) { modifier ->
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(color = Color.LightGray)
                )
            }
            is Routing.Child2 -> Child2Node(buildContext)
            is Routing.Child3 -> Child3Node(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = modifier.fillMaxSize()) {
            Children(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .requiredHeight(250.dp),
                routingSource = backStack,
                transitionHandler = rememberCombinedHandler(
                    handlers = listOf(rememberBackstackSlider(), rememberBackstackFader())
                )
            )
            Spacer(modifier = Modifier.requiredHeight(4.dp))
            Button(
                onClick = { backStack.push(Routing.Child2) },
                modifier = Modifier.padding(4.dp),
            ) {
                Text(text = "Push Child2")
            }

            Spacer(modifier = Modifier.requiredHeight(4.dp))
            Button(
                onClick = { backStack.push(Routing.Child3) },
                modifier = Modifier.padding(4.dp),
            ) {
                Text(text = "Push Child3")
            }

            Spacer(modifier = Modifier.requiredHeight(4.dp))

            Button(
                onClick = { backStack.pop() },
                modifier = Modifier.padding(4.dp),
            ) {
                Text(text = "Pop")
            }

            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(text = "Child2 info :")
            Text(text = child2InfoState)

            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(text = "Child2 and Child3 combined info :")
            Text(text = child2And3InfoState)
        }
    }
}
