package com.bumble.appyx.app.node.onboarding.screen.modeldriven

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.R
import com.bumble.appyx.app.composable.Page
import com.bumble.appyx.app.composable.graph.nodeimpl.SimpleGraphNode
import com.bumble.appyx.app.ui.AppyxSampleAppTheme
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import com.bumble.appyx.core.node.Node

@ExperimentalUnitApi
@ExperimentalComposeUiApi
class ModelDrivenIntro(
    buildContext: BuildContext,
) : Node(
    buildContext = buildContext
) {
    @Composable
    override fun View(modifier: Modifier) {
        Page(
            modifier = modifier,
            title = "Model-driven",
            body = "Navigation in Appyx is based on models – " +
                "UI & transition animations are a consequence of these models changing their state." +
                "\n\n" +
                "Go to the next page to see some examples!"
        ) {
            val image: Painter = painterResource(
                id = if (isSystemInDarkTheme()) R.drawable.graph1_dark
                else R.drawable.graph1_light
            )
            Image(
                painter = image,
                contentDescription = "logo",
                modifier = Modifier
                    .fillMaxSize(1f)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun ModelDrivenIntroScreenPreview() {
    AppyxSampleAppTheme(darkTheme = false) {
        PreviewContent()
    }
}

@Preview
@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun ModelDrivenIntroScreenPreviewDark() {
    AppyxSampleAppTheme(darkTheme = true) {
        PreviewContent()
    }
}

@Composable
@ExperimentalUnitApi
@ExperimentalComposeUiApi
private fun PreviewContent() {
    Surface(color = MaterialTheme.colors.background) {
        Box(Modifier.fillMaxSize()) {
            NodeHost(integrationPoint = IntegrationPointStub()) {
                ModelDrivenIntro(
                    root(null)
                )
            }
        }
    }
}
