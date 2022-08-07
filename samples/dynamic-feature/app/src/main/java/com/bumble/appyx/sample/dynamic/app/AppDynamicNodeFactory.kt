package com.bumble.appyx.sample.dynamic.app

import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import java.util.concurrent.ConcurrentHashMap

internal object AppDynamicNodeFactory : DynamicNodeFactory {
    private val cachedFactories: MutableMap<String, NodeFactory<*>> = ConcurrentHashMap()

    override fun isNodeAvailable(className: String): Boolean =
        getNodeFactory(className) != null

    override fun createNode(className: String, buildContext: BuildContext): Node =
        requireNotNull(getNodeFactory(className)) { "NodeFactory not found for $className" }
            .create(buildContext)

    private fun getNodeFactory(className: String): NodeFactory<*>? {
        val nodeFactory = cachedFactories[className]
        if (nodeFactory != null) {
            return nodeFactory
        }
        return try {
            (Class.forName(className).newInstance() as NodeFactory<*>)
        } catch (e: ClassNotFoundException) {
            null
        } catch (e: ClassCastException) {
            throw IllegalStateException("$className does not extend NodeFactory", e)
        }
    }
}
