package com.bumble.appyx.sandbox.client.interop.child

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.badoo.ribs.core.customisation.inflate
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder
import com.bumble.appyx.interop.ribs.ComposeRibView
import com.bumble.appyx.interop.ribs.ComposeView
import com.bumble.appyx.sandbox.client.interop.child.RibsChildView.Event
import com.bumble.appyx.sandbox.client.interop.child.RibsChildView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface RibsChildView : RibView, ObservableSource<Event>, Consumer<ViewModel> {
    sealed class Event {
        data object ButtonClicked : Event()
    }

    data class ViewModel(
        val text: String
    )

    interface Factory : ViewFactoryBuilder<Nothing?, RibsChildView>
}

class RibsChildViewImpl private constructor(
    context: Context,
    private val events: PublishRelay<Event> = PublishRelay.create(),
) : ComposeRibView(context), RibsChildView, ObservableSource<Event> by events {

    class Factory : RibsChildView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<RibsChildView> =
            ViewFactory {
                RibsChildViewImpl(it.parent.context)
            }
    }

    private var vm by mutableStateOf<ViewModel?>(null)

    override fun accept(viewModel: ViewModel) {
        vm = viewModel
    }

    override val composable: ComposeView = {
        View()
    }

    @Composable
    private fun View() {
        Column {
            Button(onClick = remember { { events.accept(Event.ButtonClicked) } }) {
                Text("Add")
            }
            Text(text = vm?.text ?: "")
        }
    }
}
