import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bumble.appyx.CreatedPlatformLifecycleOwner
import com.bumble.appyx.DesktopCoroutineScope
import com.bumble.appyx.DesktopPlatformLifecycleOwner
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPoint

fun main() = application {
    val integrationPoint = FooIntegrationPoint()
    val scope = rememberCoroutineScope()
    DesktopCoroutineScope = scope // Hack for now as this needing to be a composable is causing a lot of issues.
    CompositionLocalProvider(
        DesktopPlatformLifecycleOwner provides CreatedPlatformLifecycleOwner()
    ) {
        Window(onCloseRequest = ::exitApplication) {
            MaterialTheme {
                Column {
                    NodeHost(integrationPoint = integrationPoint) {
                        ContainerNode(buildContext = it)
                    }
                }
            }
        }
    }
}

class FooIntegrationPoint : IntegrationPoint() {
    override fun onRootFinished() {

    }

    override fun handleUpNavigation() {
    }
}
