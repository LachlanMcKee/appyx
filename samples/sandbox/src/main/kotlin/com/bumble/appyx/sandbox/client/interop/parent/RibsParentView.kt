package com.bumble.appyx.sandbox.client.interop.parent

import android.content.Context
import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.interop.ribs.ComposeRibView
import com.bumble.appyx.interop.ribs.ComposeView
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentView.Event
import com.bumble.appyx.sandbox.client.interop.parent.RibsParentView.Event.SwitchClicked
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource

interface RibsParentView : RibView, ObservableSource<Event> {

    interface Factory : ViewFactoryBuilder<Nothing?, RibsParentView>

    sealed class Event {
        object SwitchClicked : Event()
    }
}

class RibsParentViewImpl private constructor(
    context: Context,
    private val events: PublishRelay<Event> = PublishRelay.create(),
) : ComposeRibView(context), RibsParentView, ObservableSource<Event> by events {

    class Factory : RibsParentView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<RibsParentView> =
            ViewFactory {
                RibsParentViewImpl(it.parent.context)
            }
    }

    private var content: MutableState<ComposeView?> = mutableStateOf(null)

    override val composable: ComposeView = {
        View(content.value, remember { { events.accept(SwitchClicked) } })

    }

    override fun getParentViewForSubtree(subtreeOf: Node<*>): MutableState<ComposeView?> =
        content
}

@Composable
@SuppressWarnings("MagicNumber")
private fun View(content: ComposeView?, onButtonClicked: () -> Unit) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Black area belongs to 1.0, container below to 2.0", color = Color.White)
            Button(onClick = onButtonClicked) {
                Text("Push V1 or interop node")
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            content?.invoke()
        }
    }
}
