package com.bumble.appyx.core.integrationpoint

import androidx.compose.runtime.Stable
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.routing.upnavigation.UpNavigationHandler

@Stable
abstract class IntegrationPoint : UpNavigationHandler {

    private var _root: Node? = null
    private val root: Node
        get() = _root ?: error("Root has not been initialised. Did you forget to call attach?")

    fun attach(root: Node) {
        if (_root != null) error("A root has already been attached to this integration point")
        if (!root.isRoot) error("Trying to attach non-root Node")
        this._root = root
        root.integrationPoint = this
    }

    fun detach() {
        _root = null
    }

    abstract fun onRootFinished()
}
