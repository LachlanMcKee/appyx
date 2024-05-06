package com.bumble.appyx.sandbox.client.interop2

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.interop.ribs.InteropAppyxNode
import com.bumble.appyx.sandbox.client.interop.child.RibsChildBuilder
import com.bumble.appyx.sandbox.client.interop2.Interop2Node.RibsTarget
import kotlinx.parcelize.Parcelize

class Interop2Node(
    buildContext: BuildContext,
    navModel: PermanentNavModel<RibsTarget> = PermanentNavModel(
        navTargets = setOf(RibsTarget("")),
        savedStateMap = buildContext.savedStateMap
    ),
    private val ribsChildBuilder: RibsChildBuilder = RibsChildBuilder(),
) : ParentNode<RibsTarget>(navModel, buildContext) {
    @Parcelize
    data class RibsTarget(val name: String) : Parcelable

    override fun resolve(navTarget: RibsTarget, buildContext: BuildContext): Node {
        return InteropAppyxNode(buildContext, ribsChildBuilder, null)
    }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            navModel = navModel,
        )
    }
}
