package com.bumble.appyx.interop.ribs

import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.view.RibView

class InteropRibsBuilder<P, T : com.badoo.ribs.core.Rib>(
    private val buildContext: BuildContext,
    private val delegateBuilder: Builder<P, T>
) : Builder<P, T>() {

    override fun build(buildParams: BuildParams<P>): T {
        return delegateBuilder.build(buildContext, buildParams.payload)
    }

    private class DelegatingNode<V: RibView>(
        delegate: Node<V>,
    ): Node<V>(delegate.buildParams, null, emptyList()) {

    }
}
