import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.routing.transition.rememberCombinedHandler
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackSlider

class ContainerNode(
    buildContext: BuildContext, private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Picker,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<ContainerNode.Routing>(
    routingSource = backStack,
    buildContext = buildContext,
) {
    sealed class Routing {
        object Picker : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node = when (routing) {
        is Routing.Picker -> node(buildContext) { Text("Hello!") }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxSize(), routingSource = backStack, transitionHandler = rememberCombinedHandler(
                handlers = listOf(rememberBackstackSlider(), rememberBackstackFader())
            )
        ) {
            children<Routing> { child, descriptor ->
                child()
            }
        }
    }
}
