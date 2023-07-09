package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.navigation.NavModel
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.NavTarget
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event.LoadDataClicked
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event.SwitchChildClicked
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.InitialState
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.Loaded
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.Loading
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface MviCoreExampleView : Consumer<ViewModel>, ObservableSource<Event>

class MviCoreExampleViewImpl(
    private val title: String = "Title",
    private val backStack: NavModel<NavTarget, BackStack.State>,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : ParentNodeView<NavTarget>,
    MviCoreExampleView,
    ObservableSource<Event> by events {

    sealed class Event {
        object LoadDataClicked : Event()
        object SwitchChildClicked : Event()
    }

    private var vm by mutableStateOf<ViewModel?>(null)

    override fun accept(vm: ViewModel) {
        this.vm = vm
    }

    @Suppress("LongMethod")
    @Composable
    override fun ParentNode<NavTarget>.NodeView(modifier: Modifier) {
        val viewModel = vm ?: return
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier
                    .testTag(TitleTag)
            )
            Children(
                transitionHandler = rememberBackstackSlider(),
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(200.dp),
                navModel = backStack
            )
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                onClick = { events.accept(SwitchChildClicked) }
            ) {
                Text(text = "Switch between children")
            }
            when (viewModel) {
                is Loading -> Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(LoadingTestTag)
                    )
                }
                is InitialState ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                modifier = Modifier.testTag(InitialStateTextTag),
                                color = Color.Black, text = viewModel.stateName
                            )
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Button(
                                modifier = Modifier.testTag(InitialStateButtonTag),
                                onClick = { events.accept(LoadDataClicked) }
                            ) {
                                Text(
                                    modifier = Modifier.testTag(InitialStateButtonTextTag),
                                    text = "Load data"
                                )
                            }
                        }
                    }
                is Loaded ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black,
                            text = viewModel.stateName
                        )
                    }
            }
        }
    }

    companion object {
        const val TitleTag = "Title"
        const val LoadingTestTag = "Loading"
        const val InitialStateTextTag = "InitialStateText"
        const val InitialStateButtonTag = "InitialStateButton"
        const val InitialStateButtonTextTag = "InitialStateButton"
    }
}
