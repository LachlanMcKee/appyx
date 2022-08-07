package com.bumble.appyx.sample.navigtion.compose

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.routing.transition.rememberCombinedHandler
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackSlider
import kotlinx.parcelize.Parcelize

class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalIntegrationPoint provides integrationPoint,
            ) {
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        NavigationRoot()
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationRoot() {
    Text("Navigation Compose interop example")

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "google-route") {
        composable("google-route") {
            Column {
                Text("Google screen")
                Button(onClick = { navController.navigate("appyx-route") }) {
                    Text("Navigate to Appyx")
                }
            }
        }
        composable("appyx-route") {
            Column {
                Text("Appyx screen")
                Button(onClick = { navController.navigate("google-route") }) {
                    Text("Navigate to Google")
                }
                NodeHost(integrationPoint = LocalIntegrationPoint.current!!) {
                    ContainerNode(buildContext = it)
                }
            }
        }
    }
}

class ContainerNode(
    buildContext: BuildContext,
    private val backStack: BackStack<Routing> = BackStack(
        initialElement = Routing.Main,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<ContainerNode.Routing>(
    routingSource = backStack,
    buildContext = buildContext,
) {

    sealed class Routing : Parcelable {
        @Parcelize
        object Main : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Main -> node(buildContext) { Text("Appyx!!") }
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
            children<Routing> { child ->
                child()
            }
        }
    }
}
