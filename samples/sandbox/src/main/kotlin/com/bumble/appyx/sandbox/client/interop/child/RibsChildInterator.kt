package com.bumble.appyx.sandbox.client.interop.child

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject

class RibsChildInterator(
    buildParams: BuildParams<Nothing?>
) : Interactor<RibsChildNode, RibsChildView>(buildParams) {

    private val stateSubject: BehaviorSubject<RibsChildView.ViewModel> = BehaviorSubject.createDefault(
        RibsChildView.ViewModel(
            buildParams.savedInstanceState?.getString("State", "") ?: ""
        )
    )

    override fun onViewCreated(view: RibsChildView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(view to Consumer {
                stateSubject.onNext(RibsChildView.ViewModel((stateSubject.value?.text ?: "") + "1"))
            })
            bind(stateSubject to view)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("State", stateSubject.value?.text)
    }

}
