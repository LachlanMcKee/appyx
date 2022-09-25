package com.bumble.appyx.sample.dualbackstack

import androidx.compose.runtime.Stable

@Stable
data class DualBackStackViewState(
    val totalVisibleChildren: Int,
    val activeLeftPanelCount: Int,
    val allLeftPanelCount: Int,
    val activeRightPanelCount: Int,
    val allRightPanelCount: Int
)
