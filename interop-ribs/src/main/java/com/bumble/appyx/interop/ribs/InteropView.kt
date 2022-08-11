package com.bumble.appyx.interop.ribs

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.badoo.ribs.compose.ComposeRibView
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.interop.ribs.InteropView.*

interface InteropView : RibView {

    interface Dependency<N : Node> {
        val appyxNode: N
    }
}

internal class InteropViewImpl private constructor(
    override val context: Context,
    private val appyxNode: Node,
) : InteropView, ComposeRibView(context) {

    private val integrationPoint = retrieveIntegrationPoint()

    init {
        appyxNode.integrationPoint = integrationPoint
    }

    override val composable: ComposeView
        get() = @Composable {
            appyxNode.Compose()
        }

    class Factory<N : Node> : ViewFactoryBuilder<Dependency<N>, InteropView> {
        override fun invoke(deps: Dependency<N>): ViewFactory<InteropView> =
            ViewFactory {
                InteropViewImpl(
                    context = it.parent.context,
                    appyxNode = deps.appyxNode
                )
            }
    }

    private fun retrieveIntegrationPoint(): IntegrationPoint {
        val activity = context.findActivity<AppCompatActivity>()
        return if (activity is IntegrationPointAppyxProvider) {
            activity.integrationPointAppyx
        } else {
            throw IllegalStateException("Activity where InteropNode is used must implement IntegrationPointAppyxProvider")
        }
    }
}
