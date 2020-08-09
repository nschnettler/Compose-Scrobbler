package de.schnettler.scrobbler.components

import androidx.annotation.FloatRange
import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.ExponentialDecay
import androidx.compose.animation.core.OnAnimationEnd
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.fling
import androidx.compose.foundation.Box
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.offsetPx
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.lerp

private val SWIPE_DISTANCE_SIZE = 100.dp
private const val SWIPE_DOWN_OFFSET = 1.2f

@Composable
fun SwipeToRefreshLayout(
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit,
    dragCoefficient: Float = 0.5f,
    content: @Composable () -> Unit
) {
    val size = with(DensityAmbient.current) { SWIPE_DISTANCE_SIZE.toPx() }
    // min is below negative to hide
    val min = -size
    val max = size * SWIPE_DOWN_OFFSET
    val dragPosition = state { max }

    Stack(
        modifier = Modifier.stateDraggable(
            state = refreshingState,
            onStateChange = { shouldRefresh -> if (shouldRefresh) onRefresh() },
            anchorsToState = listOf(min to false, max to true),
            animationSpec = TweenSpec(),
            orientation = Orientation.Vertical,
            minValue = min,
            maxValue = max,
            onNewValue = { dragPosition.value = it * dragCoefficient }
        )
    ) {
        content()
        Box(Modifier.gravity(Alignment.TopCenter).offsetPx(y = dragPosition)) {
            if (dragPosition.value != min) {
                refreshIndicator()
            }
        }
    }
}

@Composable
fun SwipeRefreshPrograssIndicator() {
    Surface(elevation = 10.dp, shape = CircleShape) {
        CircularProgressIndicator(
            modifier = Modifier.preferredSize(40.dp).padding(6.dp),
            strokeWidth = 2.5.dp
        )
    }
}

// Copied from ui/ui-material/src/main/java/androidx/ui/material/internal/StateDraggable.kt

/**
 * Enable automatic drag and animation between predefined states.
 *
 * This can be used for example in a Switch to enable dragging between two states (true and
 * false). Additionally, it will animate correctly when the value of the state parameter is changed.
 *
 * Additional features compared to [draggable]:
 * 1. [onNewValue] provides the developer with the new value every time drag or animation (caused
 * by fling or [state] change) occurs. The developer needs to hold this state on their own
 * 2. When the anchor is reached, [onStateChange] will be called with state mapped to this anchor
 * 3. When the anchor is reached and [onStateChange] with corresponding state is called, but
 * call site didn't update state to the reached one for some reason,
 * this component performs rollback to the previous (correct) state.
 * 4. When new [state] is provided, component will be animated to state's anchor
 *
 * @param T type with which state is represented
 * @param state current state to represent Float value with
 * @param onStateChange callback to update call site's state
 * @param anchorsToState pairs of anchors to states to map anchors to state and vise versa
 * @param animationSpec animation which will be used for animations
 * @param orientation orientation of the drag
 * @param thresholds the thresholds between anchors that determine which anchor to fling to when
 * dragging stops, represented as a lambda that takes a pair of anchors and returns a value
 * between them (note that the order of the anchors matters as it indicates the drag direction)
 * @param enabled whether or not this Draggable is enabled and should consume events
 * @param reverseDirection reverse the direction of the scroll, so top to bottom scroll will
 * behave like bottom to top and left to right will behave like right to left.
 * @param minValue lower bound for draggable value in this component
 * @param maxValue upper bound for draggable value in this component
 * @param onNewValue callback to update state that the developer owns when animation or drag occurs
 */
@Suppress("LongParameterList", "LongMethod")
internal fun <T> Modifier.stateDraggable(
    state: T,
    onStateChange: (T) -> Unit,
    anchorsToState: List<Pair<Float, T>>,
    animationSpec: AnimationSpec<Float>,
    orientation: Orientation,
    thresholds: (Float, Float) -> Float = fractionalThresholds(0.5f),
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    minValue: Float = Float.MIN_VALUE,
    maxValue: Float = Float.MAX_VALUE,
    interactionState: InteractionState? = null,
    onNewValue: (Float) -> Unit
) = composed {
    val forceAnimationCheck = state { true }
    val anchors = remember(anchorsToState) { anchorsToState.map { it.first } }
    val currentValue = anchorsToState.fastFirstOrNull { it.second == state }!!.first
    val onAnimationEnd: OnAnimationEnd = { reason, finalValue, _ ->
        if (reason != AnimationEndReason.Interrupted) {
            val newState = anchorsToState.firstOrNull { it.first == finalValue }?.second
            if (newState != null && newState != state) {
                onStateChange(newState)
                forceAnimationCheck.value = !forceAnimationCheck.value
            }
        }
    }
    val flingConfig = FlingConfig(
        decayAnimation = ExponentialDecay(),
        adjustTarget = { target ->
            // Find the two anchors the target lies between.
            val a = anchors.filter { it <= target }.maxOrNull()
            val b = anchors.filter { it >= target }.maxOrNull()
            // Compute which anchor to fling to.
            val adjusted: Float =
                if (a == null && b == null) {
                    // There are no anchors, so return the target unchanged.
                    target
                } else if (a == null) {
                    // The target lies below the anchors, so return the first anchor (b).
                    b!!
                } else if (b == null) {
                    // The target lies above the anchors, so return the last anchor (b).
                    a
                } else if (a == b) {
                    // The target is equal to one of the anchors, so return the target unchanged.
                    target
                } else {
                    // The target lies strictly between the two anchors a and b.
                    // Compute the threshold between a and b based on the drag direction.
                    val threshold = if (currentValue <= a) {
                        thresholds(a, b)
                    } else {
                        thresholds(b, a)
                    }
                    require(threshold >= a && threshold <= b) {
                        "Invalid threshold $threshold between anchors $a and $b."
                    }
                    if (target < threshold) a else b
                }
            TargetAnimation(adjusted, animationSpec)
        }
    )
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val position = remember(clock) {
        onNewValue(currentValue)
        NotificationBasedAnimatedFloat(currentValue, clock, onNewValue)
    }
    position.onNewValue = onNewValue
    position.setBounds(minValue, maxValue)
    // This state is to force this component to be recomposed and trigger onCommit below
    // This is needed to stay in sync with drag state that caller side holds
    onCommit(currentValue, forceAnimationCheck.value) {
        position.animateTo(currentValue, animationSpec)
    }
    Modifier.draggable(
        orientation = orientation,
        enabled = enabled,
        reverseDirection = reverseDirection,
        startDragImmediately = position.isRunning,
        interactionState = interactionState,
        onDragStopped = {
            position.fling(
                startVelocity = it,
                decay = flingConfig.decayAnimation,
                adjustTarget = flingConfig.adjustTarget,
                onEnd = onAnimationEnd
            )
        }
    ) { delta ->
        position.snapTo(position.value + delta)
    }
}

/**
 * Fractional thresholds. Each threshold will be at a [fraction] of the way between the two anchors.
 */
internal fun fractionalThresholds(
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): (Float, Float) -> Float = { fromAnchor, toAnchor -> lerp(fromAnchor, toAnchor, fraction) }

private class NotificationBasedAnimatedFloat(
    initial: Float,
    clock: AnimationClockObservable,
    var onNewValue: (Float) -> Unit
) : AnimatedFloat(clock, Spring.DefaultDisplacementThreshold) {
    override var value = initial
        set(value) {
            onNewValue(value)
            field = value
        }
}