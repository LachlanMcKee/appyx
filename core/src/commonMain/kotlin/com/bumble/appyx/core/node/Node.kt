package com.bumble.appyx.core.node

//import androidx.annotation.CallSuper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.createLifecycleOwnerProvider
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import com.bumble.appyx.core.BuildConfig
import com.bumble.appyx.core.integrationpoint.IntegrationPoint
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.lifecycle.*
import com.bumble.appyx.core.modality.AncestryInfo
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.plugin.*
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.core.state.MutableSavedStateMapImpl
import com.bumble.appyx.core.state.SavedStateMap
import com.bumble.appyx.createPlatformLifecycleRegistry
import com.bumble.appyx.debug.Appyx

abstract class Node(
    buildContext: BuildContext,
    val view: NodeView = EmptyNodeView,
    plugins: List<Plugin> = emptyList()
) : NodeLifecycle, NodeView by view {

    @Suppress("LeakingThis") // Implemented in the same way as in androidx.Fragment
    private val nodeLifecycle = NodeLifecycleImpl(this)

    val plugins: List<Plugin> = plugins + listOfNotNull(this as? Plugin)

    val ancestryInfo: AncestryInfo =
        buildContext.ancestryInfo

    val isRoot: Boolean =
        ancestryInfo == AncestryInfo.Root

    private val parent: ParentNode<*>? =
        when (ancestryInfo) {
            is AncestryInfo.Child -> ancestryInfo.anchor
            is AncestryInfo.Root -> null
        }

    var integrationPoint: IntegrationPoint = IntegrationPointStub()
        get() {
            return if (isRoot) field
            else parent?.integrationPoint ?: error(
                "Non-root Node should have a parent"
            )
        }
        set(value) {
            check(isRoot) { "Only a root Node can have an integration point" }
            field = value
        }

    private val lifecycleRegistry = createPlatformLifecycleRegistry(this)

    private var wasBuilt = false

    init {
//        if (BuildConfig.DEBUG) {
//            lifecycle.addObserver(LifecycleLogger)
//        }
        lifecycle.addObserver(object : PlatformLifecycleObserver {
            override fun onCreate(owner: PlatformLifecycleOwner) {
                if (!wasBuilt) error("onBuilt was not invoked for $this")
            }
        });
    }

    //    @CallSuper
    open fun onBuilt() {
        require(!wasBuilt) { "onBuilt was already invoked" }
        wasBuilt = true
        updateLifecycleState(PlatformLifecycle.State.CREATED)
        plugins<NodeAware<Node>>().forEach { it.init(this) }
        plugins<NodeLifecycleAware>().forEach { it.onCreate(lifecycle) }
    }

    @Composable
    fun Compose(modifier: Modifier = Modifier) {
        CompositionLocalProvider(
            LocalNode provides this,
            createLifecycleOwnerProvider(this),
        ) {
            DerivedSetup()
            View(modifier)
        }
    }

    /** Derived classes can declare functional (non-ui) Composable blocks before [View()] is invoked. */
    @Composable
    protected open fun DerivedSetup() {
    }

    override val lifecycle: PlatformLifecycle
        get() = nodeLifecycle.lifecycle

    override fun updateLifecycleState(state: PlatformLifecycle.State) {
        if (lifecycle.currentState == state) return
        if (lifecycle.currentState == PlatformLifecycle.State.DESTROYED && state != PlatformLifecycle.State.DESTROYED) {
            Appyx.reportException(
                IllegalStateException("Trying to change lifecycle state of already destroyed node ${this::class.qualifiedName}")
            )
            return
        }
        nodeLifecycle.updateLifecycleState(state)
        if (state == PlatformLifecycle.State.DESTROYED) {
            plugins<Destroyable>().forEach { it.destroy() }
        }
    }

    fun saveInstanceState(scope: SaverScope): SavedStateMap {
        val writer = MutableSavedStateMapImpl(saverScope = scope)
        onSaveInstanceState(writer)
        plugins
            .filterIsInstance<SavesInstanceState>()
            .forEach { it.saveInstanceState(writer) }
        return writer.savedState
    }

    //    @CallSuper
    protected open fun onSaveInstanceState(state: MutableSavedStateMap) {
        // no-op
    }

    fun finish() {
        parent?.onChildFinished(this) ?: integrationPoint.onRootFinished()
    }

    /**
     * Triggers parents up navigation (back navigation by default).
     *
     * This method is useful for different cases like:
     * - Close button on the screen which leads back to the previous screen.
     * - Blocker screen that intercepts back button clicks but closes itself when condition is met.
     *
     * To properly handle blocker case this method skips the current node plugins (like router),
     * and invokes the parent directly.
     */
    fun navigateUp() {
        require(parent != null || isRoot) {
            "Can't navigate up, neither parent nor integration point is presented"
        }
        if (parent?.performUpNavigation() != true) {
            integrationPoint.handleUpNavigation()
        }
    }

    //    @CallSuper
    protected open fun performUpNavigation(): Boolean =
        handleUpNavigationByPlugins() || parent?.performUpNavigation() == true

    private fun handleUpNavigationByPlugins(): Boolean =
        plugins<UpNavigationHandler>().any { it.handleUpNavigation() }

}
