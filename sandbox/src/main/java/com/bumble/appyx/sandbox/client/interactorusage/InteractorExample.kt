package com.bumble.appyx.sandbox.client.interactorusage

import com.bumble.appyx.core.children.whenChildAttached
import com.bumble.appyx.core.children.whenChildrenAttached
import com.bumble.appyx.core.clienthelper.interactor.Interactor
import com.bumble.appyx.core.lifecycle.PlatformLifecycle

class InteractorExample : Interactor<InteractorExampleNode>() {

    override fun onCreate(lifecycle: PlatformLifecycle) {
        lifecycle.subscribe(onCreate = {
            whenChildAttached { _: PlatformLifecycle, _: Child2Node ->
                node.child2InfoState = "Child2 has been attached"
            }
            whenChildrenAttached { _: PlatformLifecycle, _: Child2Node, _: Child3Node ->
                node.child2And3InfoState = "Child2 and Child3 have been attached"
            }
        })
    }
}
