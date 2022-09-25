package com.bumble.appyx.navmodel.dualbackstack.transitionhandler

import android.annotation.SuppressLint
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.dualbackstack.DualBackStack

@Suppress("TransitionPropertiesLabel")
class DualBackStackFader<T>(
    private val transitionSpec: TransitionSpec<DualBackStack.State, Float> = { spring() }
) : ModifierTransitionHandler<T, DualBackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<DualBackStack.State>,
        descriptor: TransitionDescriptor<T, DualBackStack.State>
    ): Modifier = modifier.composed {
        val alpha = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    DualBackStack.State.Active1 -> 1f
                    DualBackStack.State.Active2 -> 1f
                    else -> 0f
                }
            })

        alpha(alpha.value)
    }
}

@Composable
fun <T> rememberDualBackstackFader(
    transitionSpec: TransitionSpec<DualBackStack.State, Float> = { spring() }
): ModifierTransitionHandler<T, DualBackStack.State> = remember {
    DualBackStackFader(transitionSpec = transitionSpec)
}
