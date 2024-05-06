package com.bumble.appyx.interop.ribs

import android.content.Context
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.interop.ribs.InteropView.Dependency

interface InteropView : RibView {

    interface Dependency<N : Node> {
        val appyxNode: N
        val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner
    }
}

internal class InteropViewImpl private constructor(
    override val context: Context,
    private val appyxNode: Node,
    private val onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner,
) : InteropView, ComposeRibView(context) {

    override val composable: ComposeView
        get() = @Composable {
            CompositionLocalProvider(
                LocalOnBackPressedDispatcherOwner provides onBackPressedDispatcherOwner,
            ) {
                Column {
                    Text("RIBs interop wrapping Appyx")
                    appyxNode.Compose()
                }
            }
        }

    class Factory<N : Node> : ViewFactoryBuilder<Dependency<N>, InteropView> {
        override fun invoke(deps: Dependency<N>): ViewFactory<InteropView> =
            ViewFactory {
                InteropViewImpl(
                    context = it.parent.context,
                    appyxNode = deps.appyxNode,
                    onBackPressedDispatcherOwner = deps.onBackPressedDispatcherOwner,
                )
            }
    }
}
