package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.mvicore.feature.Feature
import com.bumble.appyx.core.clienthelper.interactor.Interactor
import com.bumble.appyx.core.lifecycle.PlatformLifecycle
import com.bumble.appyx.core.lifecycle.android.toAndroidLifecycle
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.EventsToWish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.StateToViewModel

class MviCoreLeafInteractor(
    private val view: MviCoreLeafView,
    private val feature: Feature<Wish, State, News>
) : Interactor<MviCoreLeafNode>() {

    override fun onCreate(lifecycle: PlatformLifecycle) {
        lifecycle.toAndroidLifecycle().startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using EventsToWish)
        }
    }
}
