package com.bumble.appyx.sample.dynamic.app

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

internal interface DynamicNodeFactory {
    fun isNodeAvailable(className: String): Boolean
    fun createNode(className: String, buildContext: BuildContext): Node
}
