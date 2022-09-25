package com.bumble.appyx.sample.dualbackstack

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun PanelControls(
    viewState: DualBackStackViewState,
    pushLeftClick: () -> Unit,
    pushRightClick: () -> Unit,
    popLeftClick: () -> Unit,
    popRightClick: () -> Unit,
    popClick: () -> Unit,
) {
    Row {
        Button(onClick = pushLeftClick) {
            Text("Add Panel 1")
        }
        Button(onClick = pushRightClick) {
            Text("Add Panel 2")
        }
    }
    Row {
        Button(
            onClick = popLeftClick,
            enabled = viewState.allLeftPanelCount > 1,
            content = {
                Text("Pop Panel 1")
            }
        )
        Button(
            onClick = popRightClick,
            enabled = viewState.allRightPanelCount > 0,
            content = {
                Text("Pop Panel 2")
            }
        )
        Button(
            onClick = popClick,
            enabled = viewState.allLeftPanelCount > 1 || viewState.allRightPanelCount > 0,
            content = {
                Text("Pop")
            }
        )
    }
}
