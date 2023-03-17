package com.bumble.appyx.interactions.sample.testdrive.helper

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.setupTestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDrive
import com.bumble.appyx.transitionmodel.testdrive.TestDriveModel
import com.bumble.appyx.transitionmodel.testdrive.interpolator.TestDriveMotionController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


fun ComposeContentTestRule.createTestDrive(
    animationSpec: AnimationSpec<Float> = tween(
        durationMillis = 1000,
        easing = LinearEasing
    )
): TestDrive<NavTarget> {
    val model = TestDriveModel(NavTarget.Child1)
    return TestDrive(
        scope = CoroutineScope(Dispatchers.Unconfined),
        model = model,
        motionController = { TestDriveMotionController(it) },
        progressAnimationSpec = animationSpec,
        gestureFactory = { TestDriveMotionController.Gestures(it) },
    ).also { setupTestDrive(it, model) }
}


