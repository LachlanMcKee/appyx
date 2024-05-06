package com.bumble.appyx.sandbox.client.interop.child

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.interop.ribs.ComposeRibView
import com.bumble.appyx.interop.ribs.ComposeView

interface RibsChildView : RibView {

    interface Factory : ViewFactoryBuilder<Nothing?, RibsChildView>
}

class RibsChildViewImpl private constructor(
    context: Context
) : ComposeRibView(context), RibsChildView {

    class Factory : RibsChildView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<RibsChildView> =
            ViewFactory {
                RibsChildViewImpl(it.parent.context)
            }
    }

    override val composable: ComposeView = {
        val state = remember { mutableStateListOf<String>() }
        Column {
            Button(onClick = { state.add((state.size + 1).toString()) }) {
                Text("Add")
            }
            Text(text = state.joinToString())
        }
    }
}
