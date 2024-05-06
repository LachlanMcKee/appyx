package com.bumble.appyx.sandbox.client.interop.child

import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.RibView

class RibsChildNode internal constructor(buildParams: BuildParams<*>, plugins: List<Plugin>) :
    Node<RibView>(
        buildParams = buildParams,
        viewFactory = RibsChildViewImpl.Factory().invoke(null),
        plugins = plugins
    ), Rib
