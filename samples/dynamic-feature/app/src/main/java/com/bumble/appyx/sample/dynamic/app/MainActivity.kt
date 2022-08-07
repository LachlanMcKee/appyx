package com.bumble.appyx.sample.dynamic.app

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.routing.transition.rememberCombinedHandler
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.operation.push
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackSlider
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.android.play.core.splitinstall.testing.FakeSplitInstallManagerFactory
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.disposeOnCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.parcelize.Parcelize
import kotlin.coroutines.resume

@OptIn(InternalCoroutinesApi::class)
internal class MainActivity : NodeActivity() {

    private val splitInstallManager: SplitInstallManager by lazy {
        FakeSplitInstallManagerFactory.create(this, getExternalFilesDir("local_testing"))
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                Column {
                    NodeHost(integrationPoint = integrationPoint) {
                        ContainerNode(
                            buildContext = it,
                            featureInstallFunc = { installFeature() })
                    }
                }
            }
        }
    }

    private suspend fun installFeature(): Boolean =
        suspendCancellableCoroutine { cont ->
            // Initializes a variable to later track the session ID for a given request.
            var mySessionId = 0

            // Creates a listener for request status updates.
            val listener = SplitInstallStateUpdatedListener { state ->
                if (state.sessionId() == mySessionId) {
                    // Read the status of the request to handle the state update.
                    when (state.status()) {
                        SplitInstallSessionStatus.INSTALLED -> cont.resume(true)
                        SplitInstallSessionStatus.DOWNLOADED,
                        SplitInstallSessionStatus.DOWNLOADING,
                        SplitInstallSessionStatus.INSTALLING,
                        SplitInstallSessionStatus.PENDING -> Unit
                        SplitInstallSessionStatus.CANCELED,
                        SplitInstallSessionStatus.CANCELING,
                        SplitInstallSessionStatus.FAILED,
                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION,
                        SplitInstallSessionStatus.UNKNOWN -> cont.resume(false)
                    }
                }
            }
            splitInstallManager.registerListener(listener)

            splitInstallManager
                .startInstall(
                    SplitInstallRequest.newBuilder().addModule("feature")
                        .build()
                )
                .addOnSuccessListener { sessionId ->
                    mySessionId = sessionId
                }
                .addOnFailureListener {
                    cont.resume(false)
                }

            cont.disposeOnCancellation {
                splitInstallManager.unregisterListener(listener)
            }
        }
}

class ContainerNode(
    buildContext: BuildContext,
    private val featureInstallFunc: suspend () -> Boolean,
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

        @Parcelize
        object Feature : Routing()
    }

    override fun resolve(routing: Routing, buildContext: BuildContext): Node =
        when (routing) {
            is Routing.Main -> node(buildContext) {
                Column {
                    Text("Appyx!!")
                    var showInstallationDialog by remember { mutableStateOf(false) }
                    var installingFeature by remember { mutableStateOf(false) }
                    val coroutineScope = rememberCoroutineScope()
                    if (showInstallationDialog) {
                        RequestFeatureDialog { installFeature ->
                            if (installFeature && !installingFeature) {
                                installingFeature = true
                                coroutineScope.launch {
                                    val installResult = featureInstallFunc()
                                    installingFeature = false
                                    if (installResult) {
                                        backStack.push(Routing.Feature)
                                    }
                                }
                            }
                            showInstallationDialog = false
                        }
                    }
                    if (installingFeature) {
                        LoadingDialog()
                    }
                    Button(onClick = {
                        if (AppDynamicNodeFactory.isNodeAvailable("com.bumble.appyx.sample.dynamic.feature.FeatureNodeFactoryImpl")) {
                            backStack.push(Routing.Feature)
                        } else {
                            showInstallationDialog = true
                        }
                    }) {
                        Text("Load feature")
                    }
                }
            }
            is Routing.Feature -> AppDynamicNodeFactory.createNode(
                "com.bumble.appyx.sample.dynamic.feature.FeatureNodeFactoryImpl",
                buildContext
            )
        }

    @Composable
    fun RequestFeatureDialog(onDismiss: (Boolean) -> Unit) {
        AlertDialog(
            onDismissRequest = {
                onDismiss(false)
            },
            title = {
                Text(text = "Feature unavailable")
            },
            text = {
                Text("Would you like to download the feature?")
            },
            confirmButton = {
                Button(onClick = { onDismiss(true) }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss(false) }) {
                    Text("No")
                }
            }
        )
    }

    @Composable
    fun LoadingDialog() {
        Dialog(
            onDismissRequest = { },
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(White, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator()
            }
        }
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
