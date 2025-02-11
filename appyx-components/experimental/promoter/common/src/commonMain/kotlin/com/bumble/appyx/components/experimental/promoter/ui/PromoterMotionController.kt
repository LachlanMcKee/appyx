@file:Suppress("MagicNumber")
package com.bumble.appyx.components.experimental.promoter.ui

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.experimental.promoter.PromoterModel
import com.bumble.appyx.components.experimental.promoter.PromoterModel.State.ElementState
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.helper.DefaultAnimationSpec
import com.bumble.appyx.interactions.core.ui.property.impl.AngularPosition
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.RotationZ
import com.bumble.appyx.interactions.core.ui.property.impl.Scale
import com.bumble.appyx.interactions.core.ui.property.impl.position.BiasAlignment.InsideAlignment.Companion.Center
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionInside
import com.bumble.appyx.interactions.core.ui.state.MatchedTargetUiState
import com.bumble.appyx.transitionmodel.BaseMotionController
import kotlin.math.min

@Suppress("TransitionPropertiesLabel")
class PromoterMotionController<InteractionTarget : Any>(
    uiContext: UiContext,
    uiAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
    childSize: Dp,
) : BaseMotionController<InteractionTarget, PromoterModel.State<InteractionTarget>, MutableUiState, TargetUiState>(
    uiContext = uiContext,
    defaultAnimationSpec = uiAnimationSpec,
) {
    override fun PromoterModel.State<InteractionTarget>.toUiTargets(
    ): List<MatchedTargetUiState<InteractionTarget, TargetUiState>> =
        elements.map {
            MatchedTargetUiState(
                it.first, when (it.second) {
                    ElementState.CREATED -> created
                    ElementState.STAGE1 -> stage1
                    ElementState.STAGE2 -> stage2
                    ElementState.STAGE3 -> stage3
                    ElementState.STAGE4 -> stage4
                    ElementState.STAGE5 -> stage5
                    else -> destroyed
                }
            )
        }

    override fun mutableUiStateFor(
        uiContext: UiContext,
        targetUiState: TargetUiState
    ): MutableUiState =
        targetUiState.toMutableState(uiContext)

    private val halfWidthDp = uiContext.transitionBounds.widthDp.value / 2
    private val halfHeightDp = uiContext.transitionBounds.heightDp.value / 2
    @Suppress("MaxLineLength", "UnusedPrivateMember")
    private val center = DpOffset(halfWidthDp.dp, halfHeightDp.dp) - DpOffset((childSize.value / 2).dp, (childSize.value / 2).dp)
    private val radius = min(halfWidthDp, halfHeightDp) * 0.8f

    private val created = TargetUiState(
        position = PositionInside.Target(alignment = Center),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                radius = radius,
                angleDegrees = 0f
            )
        ),
        scale = Scale.Target(0f),
        rotationY = RotationY.Target(0f),
        rotationZ = RotationZ.Target(0f),
    )

    private val stage1 = TargetUiState(
        position = PositionInside.Target(alignment = Center),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                radius = radius,
                angleDegrees = 0f
            )
        ),
        scale = Scale.Target(0.25f),
        rotationY = RotationY.Target(0f),
        rotationZ = RotationZ.Target(0f),

        )

    private val stage2 = TargetUiState(
        position = PositionInside.Target(alignment = Center),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                radius = radius,
                angleDegrees = 90f
            )
        ),
        scale = Scale.Target(0.45f),
        rotationY = RotationY.Target(0f),
        rotationZ = RotationZ.Target(0f),
    )

    private val stage3 = TargetUiState(
        position = PositionInside.Target(alignment = Center),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                radius = radius,
                angleDegrees = 180f
            )
        ),
        scale = Scale.Target(0.65f),
        rotationY = RotationY.Target(0f),
        rotationZ = RotationZ.Target(0f),
    )

    private val stage4 = TargetUiState(
        position = PositionInside.Target(alignment = Center),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                radius = radius,
                angleDegrees = 270f
            )
        ),
        scale = Scale.Target(0.85f),
        rotationY = RotationY.Target(0f),
        rotationZ = RotationZ.Target(0f),
    )

    private val stage5 = TargetUiState(
        position = PositionInside.Target(alignment = Center),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                radius = 0f,
                angleDegrees = 270f
            )
        ),
        scale = Scale.Target(1f),
        rotationY = RotationY.Target(360f),
        rotationZ = RotationZ.Target(0f),
    )

    private val destroyed = TargetUiState(
        position = PositionInside.Target(alignment = Center, offset = DpOffset(500.dp, (-200).dp)),
        angularPosition = AngularPosition.Target(
            AngularPosition.Value(
                radius = radius,
                angleDegrees = 0f
            )
        ),
        scale = Scale.Target(0f),
        rotationY = RotationY.Target(360f),
        rotationZ = RotationZ.Target(540f),
    )
}
