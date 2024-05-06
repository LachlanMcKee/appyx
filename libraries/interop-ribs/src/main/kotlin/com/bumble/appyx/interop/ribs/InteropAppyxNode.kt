package com.bumble.appyx.interop.ribs

import android.content.Context
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.plugin.ViewAware
import com.badoo.ribs.core.view.RibView
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.EmptyNodeView
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.NodeView
import com.bumble.appyx.core.plugin.Plugin

class InteropAppyxNode<P, T : Rib>(
    buildContext: BuildContext,
    builder: com.badoo.ribs.builder.Builder<P, T>,
    params: P,
    view: NodeView = EmptyNodeView,
    plugins: List<Plugin> = emptyList()
) : Node(buildContext, view, plugins), ViewAware<ComposeRibView> {

    private val rib: Rib =
        builder.build(
            buildContext = com.badoo.ribs.core.modality.BuildContext.root(null),
            payload = params,
            extraPlugins = listOf(this)
        )

    private var viewComposable by mutableStateOf<ComposeView?>(null)

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                rib.node.onCreate()
            }
        })
    }

    override fun onViewCreated(view: ComposeRibView, viewLifecycle: Lifecycle) {
        viewComposable = view.composable
    }

    private inner class FakeParentRibView(override val context: Context) : RibView {
        override val androidView: ViewGroup
            get() = error("Should not be called")

        override fun attachChild(
            child: com.badoo.ribs.core.Node<*>,
            subtreeOf: com.badoo.ribs.core.Node<*>
        ) {
//            TODO("Not yet implemented")
        }

        override fun detachChild(
            child: com.badoo.ribs.core.Node<*>,
            subtreeOf: com.badoo.ribs.core.Node<*>
        ) {
//            TODO("Not yet implemented")
        }

    }

    @Composable
    override fun View(modifier: Modifier) {
        val context = LocalContext.current
        DisposableEffect(context) {
            rib.node.onCreateView(FakeParentRibView(context))
            rib.node.onAttachToView()
            onDispose {
                rib.node.onDetachFromView()
            }
        }
        viewComposable?.invoke()
    }

}
