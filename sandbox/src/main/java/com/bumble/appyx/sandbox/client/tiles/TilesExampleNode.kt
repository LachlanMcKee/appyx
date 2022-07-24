package com.bumble.appyx.sandbox.client.tiles

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.routingsource.tiles.Tiles
import com.bumble.appyx.routingsource.tiles.operation.removeSelected
import com.bumble.appyx.routingsource.tiles.operation.toggleSelection
import com.bumble.appyx.routingsource.tiles.transitionhandler.rememberTilesTransitionHandler
import com.bumble.appyx.core.composable.Child
import com.bumble.appyx.core.composable.visibleChildrenAsState
import com.bumble.appyx.core.lifecycle.PlatformLifecycle
import com.bumble.appyx.core.lifecycle.PlatformLifecycleObserver
import com.bumble.appyx.core.lifecycle.PlatformLifecycleOwner
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.sandbox.client.child.ChildNode
import com.bumble.appyx.sandbox.client.tiles.TilesExampleNode.Routing
import com.bumble.appyx.sandbox.client.tiles.TilesExampleNode.Routing.Child1
import com.bumble.appyx.sandbox.client.tiles.TilesExampleNode.Routing.Child2
import com.bumble.appyx.sandbox.client.tiles.TilesExampleNode.Routing.Child3
import com.bumble.appyx.sandbox.client.tiles.TilesExampleNode.Routing.Child4

class TilesExampleNode(
    buildContext: BuildContext,
    private val tiles: Tiles<Routing> = Tiles(
        initialItems = listOf(
            Child1, Child2, Child3, Child4
        )
    ),
) : ParentNode<Routing>(
    routingSource = tiles,
    buildContext = buildContext,
) {

    enum class Routing {
        Child1, Child2, Child3, Child4,
    }


    override fun onBuilt() {
        super.onBuilt()
        whenChildrenAttached { commonLifecycle: PlatformLifecycle, child1: ChildNode, child2: ChildNode ->
            commonLifecycle.addObserver(object : PlatformLifecycleObserver {
                override fun onCreate(owner: PlatformLifecycleOwner) {
                    Log.d("TilesExampleNode", "Children $child1 and $child2 were connected")
                }

                override fun onDestroy(owner: PlatformLifecycleOwner) {
                    Log.d("TilesExampleNode", "Children $child1 and $child2 were disconnected")
                }
            })
        }
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            Child1 -> ChildNode("1", buildContext)
            Child2 -> ChildNode("2", buildContext)
            Child3 -> ChildNode("3", buildContext)
            Child4 -> ChildNode("4", buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Button(
                onClick = { tiles.removeSelected() },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(text = "Remove selected")
            }

            val elements by tiles.visibleChildrenAsState()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(top = 60.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {

                items(elements) { element ->
                    Child(
                        routingElement = element,
                        transitionHandler = rememberTilesTransitionHandler()
                    ) { child, _ ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clickable {
                                tiles.toggleSelection(element.key)
                            }
                        ) {
                            child()
                        }
                    }
                }
            }

        }
    }
}
