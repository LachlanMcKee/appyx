package com.bumble.appyx.transitionmodel.testdrive.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.BaseOperation
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel

@Parcelize
data class Next<NavTarget>(
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<TestDriveModel.State<NavTarget>>() {

    override fun isApplicable(state: TestDriveModel.State<NavTarget>): Boolean =
        true

    override fun createFromState(baseLineState: TestDriveModel.State<NavTarget>): TestDriveModel.State<NavTarget> =
        baseLineState

    override fun createTargetState(fromState: TestDriveModel.State<NavTarget>): TestDriveModel.State<NavTarget> =
        fromState.copy(
            elementState = fromState.elementState.next()
        )
}

fun <NavTarget : Any> TestDrive<NavTarget>.next(
    mode: Operation.Mode = Operation.Mode.KEYFRAME,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec
) {
    operation(Next(mode), animationSpec)
}