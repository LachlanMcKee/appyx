package com.bumble.appyx.interop.ribs

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
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
import com.bumble.appyx.core.state.MutableSavedStateMap

class InteropAppyxNode<P, T : Rib>(
    buildContext: BuildContext,
    builder: com.badoo.ribs.builder.Builder<P, T>,
    params: P,
) : Node(buildContext, EmptyNodeView, emptyList()), ViewAware<ComposeRibView> {

    private val rib: Rib =
        builder.build(
            buildContext = com.badoo.ribs.core.modality.BuildContext.root(
                savedInstanceState = buildContext.savedStateMap?.let { savedStateMap ->
                    bundleOf(*savedStateMap.toList().toTypedArray())
                },
                customisations = buildContext.customisations,
                defaultPlugins = { listOf(this) }
            ),
            payload = params,
        )

    private var viewComposable by mutableStateOf<ComposeView?>(null)

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                rib.node.onCreate()
            }

            override fun onStart(owner: LifecycleOwner) {
                rib.node.onStart()
            }

            override fun onStop(owner: LifecycleOwner) {
                rib.node.onStop()
            }
        })
    }

    override fun onViewCreated(view: ComposeRibView, viewLifecycle: Lifecycle) {
        viewComposable = view.composable
    }

    override fun onSaveInstanceState(state: MutableSavedStateMap) {
        super.onSaveInstanceState(state)
        val outState = Bundle()
        rib.node.onSaveInstanceState(outState)
        outState.keySet().forEach { key ->
            state[key] = outState.get(key)
        }
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
                rib.node.onDestroy(isRecreating = false) // TODO
            }
        }
        viewComposable?.invoke()
    }

}
