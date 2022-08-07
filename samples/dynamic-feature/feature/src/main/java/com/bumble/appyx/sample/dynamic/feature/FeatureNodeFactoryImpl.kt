package com.bumble.appyx.sample.dynamic.feature

import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.modality.BuildContext

internal class FeatureNodeFactoryImpl : NodeFactory<FeatureNode> {
    override fun create(buildContext: BuildContext): FeatureNode = FeatureNode(buildContext)
}
