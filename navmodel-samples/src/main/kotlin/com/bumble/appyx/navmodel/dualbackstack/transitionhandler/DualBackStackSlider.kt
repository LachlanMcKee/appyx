package com.bumble.appyx.navmodel.dualbackstack.transitionhandler

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack
import com.bumble.appyx.navmodel.dualbackstack.operation.BackStackOperation
import com.bumble.appyx.navmodel.dualbackstack.operation.PanelModeChanged
import com.bumble.appyx.navmodel.dualbackstack.operation.Pop
import com.bumble.appyx.navmodel.dualbackstack.operation.PopLeft
import com.bumble.appyx.navmodel.dualbackstack.operation.PopRight
import com.bumble.appyx.navmodel.dualbackstack.operation.Push
import kotlin.math.roundToInt

@Suppress("TransitionPropertiesLabel")
class DualBackStackSlider<T>(
    private val transitionSpec: TransitionSpec<DualBackStack.State, Offset> = {
        spring(stiffness = Spring.StiffnessVeryLow)
    },
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, DualBackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<DualBackStack.State>,
        descriptor: TransitionDescriptor<T, DualBackStack.State>
    ): Modifier = modifier.composed {
        val offset = transition.animateOffset(
            transitionSpec = transitionSpec,
            targetValueByState = {
                val width = descriptor.params.bounds.width.value
                when (it) {
                    DualBackStack.State.Created1 -> toOutsideRight(width)
                    DualBackStack.State.Created2 -> toOutsideRight(width)
                    DualBackStack.State.Active1 -> toCenter()
                    DualBackStack.State.Active2 -> toCenter()
                    DualBackStack.State.StashedInBackStack1 -> toOutsideLeft(width)
                    DualBackStack.State.StashedInBackStack2 -> toOutsideLeft(width)
                    DualBackStack.State.Destroyed1, DualBackStack.State.Destroyed2 -> {
                        when (val operation = descriptor.operation as? BackStackOperation<*>) {
                            is Push,
                            is Pop,
                            is PopLeft,
                            is PopRight -> toOutsideRight(width)
                            is PanelModeChanged -> toOutsideLeft(width) // TODO?!
                            null -> error("Unexpected operation: $operation")
                        }
                    }
                }
            })

        offset {
            IntOffset(
                x = (offset.value.x * this.density).roundToInt(),
                y = (offset.value.y * this.density).roundToInt()
            )
        }
    }

    private fun toOutsideRight(width: Float) = Offset(1.0f * width, 0f)

    private fun toOutsideLeft(width: Float) = Offset(-1.0f * width, 0f)

    private fun toCenter() = Offset(0f, 0f)
}

@Composable
fun <T> rememberDualBackstackSlider(
    transitionSpec: TransitionSpec<DualBackStack.State, Offset> = { spring(stiffness = Spring.StiffnessVeryLow) },
    clipToBounds: Boolean = false
): ModifierTransitionHandler<T, DualBackStack.State> = remember {
    DualBackStackSlider(transitionSpec = transitionSpec, clipToBounds = clipToBounds)
}
