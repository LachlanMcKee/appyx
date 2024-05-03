package com.bumble.appyx.sandbox.client.interop2

import android.os.Parcelable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.sandbox.client.interop2.Interop2Node.RibsTarget
import kotlinx.parcelize.Parcelize

class Interop2Node(
    buildContext: BuildContext,
    navModel: PermanentNavModel<RibsTarget> = PermanentNavModel(
        navTargets = setOf(RibsTarget("")),
        savedStateMap = buildContext.savedStateMap
    )
) : ParentNode<RibsTarget>(navModel, buildContext) {
    @Parcelize
    data class RibsTarget(val name: String) : Parcelable

    override fun resolve(navTarget: RibsTarget, buildContext: BuildContext): Node {
        return node(buildContext) {
            Text(text = "hi")
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Text(text = "Hi")
    }
}
