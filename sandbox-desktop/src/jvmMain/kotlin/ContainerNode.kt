import ContainerNode.Routing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.routing.transition.rememberCombinedHandler
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.sandbox.client.combined.CombinedRoutingSourceNode
import com.bumble.appyx.sandbox.client.interactorusage.InteractorNodeBuilder
import com.bumble.appyx.sandbox.client.spotlight.SpotlightExampleNode
import com.bumble.appyx.utils.customisations.NodeCustomisation

class ContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Picker,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<Routing>(
    routingSource = backStack,
    buildContext = buildContext,
) {
    class Customisation(val name: String? = null) : NodeCustomisation

    private val label: String? = buildContext.getOrDefault(Customisation()).name

    sealed class Routing {
        object Picker : Routing()
        object CombinedRoutingSource : Routing()
        object RoutingSourcesExamples : Routing()
        object SpotlightExample : Routing()
        object InteractorExample : Routing()
        object Customisations : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Picker -> node(buildContext) { modifier -> ExamplesList(modifier) }
            is Routing.RoutingSourcesExamples -> node(buildContext) { modifier -> RoutingSources(modifier) }
            is Routing.CombinedRoutingSource -> CombinedRoutingSourceNode(buildContext)
            is Routing.SpotlightExample -> SpotlightExampleNode(buildContext)
            is Routing.InteractorExample -> InteractorNodeBuilder().build(buildContext)
            is Routing.Customisations -> node(buildContext) { modifier -> Customisations(modifier) }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxSize(),
            routingSource = backStack,
            transitionHandler = rememberCombinedHandler(
                handlers = listOf(rememberBackstackSlider(), rememberBackstackFader())
            )
        ) {
            children<Routing> { child, descriptor ->
                val color = when (descriptor.element) {
                    else -> Color.White
                }
                child(modifier = Modifier.background(color))
            }
        }
    }

    @Composable
    fun ExamplesList(modifier: Modifier) {
        val scrollState = rememberScrollState()
        Box(
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                label?.let {
                    Text(it, textAlign = TextAlign.Center)
                }
                TextButton("Customisations Example") { backStack.push(Routing.Customisations) }
                TextButton("Routing Sources Examples") { backStack.push(Routing.RoutingSourcesExamples) }
            }
        }
    }

    @Composable
    fun RoutingSources(modifier: Modifier) {
        val scrollState = rememberScrollState()
        Box(
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton("Combined routing source") { backStack.push(Routing.CombinedRoutingSource) }
                TextButton("Node with interactor") { backStack.push(Routing.InteractorExample) }
                TextButton("Spotlight Example") { backStack.push(Routing.SpotlightExample) }
            }
        }
    }

    @Composable
    fun Customisations(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Not yet implemented")
        }
    }

    @Composable
    private fun TextButton(text: String, onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text(textAlign = TextAlign.Center, text = text)
        }
    }

}
