package com.bumble.appyx.core.integrationpoint

import android.os.Bundle
import androidx.compose.runtime.Stable
import com.bumble.appyx.core.integrationpoint.activitystarter.ActivityStarter
import com.bumble.appyx.core.integrationpoint.permissionrequester.PermissionRequester
import com.bumble.appyx.core.integrationpoint.requestcode.RequestCodeRegistry
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.routing.upnavigation.UpNavigationHandler

@Stable
abstract class AndroidIntegrationPoint(
    protected val savedInstanceState: Bundle?
) : IntegrationPoint() {

    protected val requestCodeRegistry = RequestCodeRegistry(savedInstanceState)

    abstract val activityStarter: ActivityStarter

    abstract val permissionRequester: PermissionRequester

    private var _root: Node? = null
    private val root: Node
        get() = _root ?: error("Root has not been initialised. Did you forget to call attach?")

    fun onSaveInstanceState(outState: Bundle) {
        requestCodeRegistry.onSaveInstanceState(outState)
    }
}
